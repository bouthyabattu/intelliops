import http from 'http';
import { WebSocketServer, WebSocket } from 'ws';

const PORT = parseInt(process.env.PORT || '4000', 10);

interface ClientContext {
  ws: WebSocket;
  id: string;
  userId?: string;
  orgId?: string;
  subscriptions: Set<string>;
  isAlive: boolean;
}

const clients = new Map<WebSocket, ClientContext>();
const rooms = new Map<string, Set<WebSocket>>();

const server = http.createServer((req, res) => {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  if (req.url === '/health' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      status: 'UP',
      service: 'intelliops-realtime-service',
      activeConnections: clients.size,
      activeRooms: rooms.size
    }));
    return;
  }

  // HTTP Broadcast webhook from Core or AI Service
  if (req.url === '/api/v1/broadcast' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body);
        const { room, event, data } = payload;
        broadcastToRoom(room, { event, data, timestamp: new Date().toISOString() });
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, deliveredTo: rooms.get(room)?.size || 0 }));
      } catch (err: any) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, error: err.message }));
      }
    });
    return;
  }

  res.writeHead(404);
  res.end('Not Found');
});

const wss = new WebSocketServer({ server });

function broadcastToRoom(room: string, message: any, excludeWs?: WebSocket) {
  const subscribers = rooms.get(room);
  if (!subscribers) return;

  const payload = JSON.stringify(message);
  for (const clientWs of subscribers) {
    if (clientWs !== excludeWs && clientWs.readyState === WebSocket.OPEN) {
      clientWs.send(payload);
    }
  }
}

wss.on('connection', (ws: WebSocket, req) => {
  const clientId = Math.random().toString(36).substring(2, 11);
  const context: ClientContext = {
    ws,
    id: clientId,
    subscriptions: new Set(),
    isAlive: true
  };
  clients.set(ws, context);

  // Send welcome handshake
  ws.send(JSON.stringify({
    event: 'connected',
    clientId,
    timestamp: new Date().toISOString(),
    availableRooms: ['tasks:global', 'incidents:live', 'presence:general', 'copilot:stream']
  }));

  ws.on('pong', () => {
    context.isAlive = true;
  });

  ws.on('message', (raw: Buffer) => {
    try {
      const msg = JSON.parse(raw.toString());
      const { action, room, data } = msg;

      if (action === 'subscribe' && room) {
        context.subscriptions.add(room);
        if (!rooms.has(room)) {
          rooms.set(room, new Set());
        }
        rooms.get(room)!.add(ws);

        ws.send(JSON.stringify({
          event: 'subscribed',
          room,
          activeInRoom: rooms.get(room)!.size
        }));

        // Broadcast presence
        broadcastToRoom(room, {
          event: 'user_joined',
          clientId: context.id,
          userId: context.userId,
          room
        }, ws);
      }

      else if (action === 'unsubscribe' && room) {
        context.subscriptions.delete(room);
        rooms.get(room)?.delete(ws);
        ws.send(JSON.stringify({ event: 'unsubscribed', room }));
      }

      else if (action === 'publish' && room) {
        broadcastToRoom(room, {
          event: msg.event || 'message',
          room,
          senderId: context.id,
          data,
          timestamp: new Date().toISOString()
        }, ws);
      }

      else if (action === 'identify') {
        context.userId = data?.userId;
        context.orgId = data?.orgId;
        ws.send(JSON.stringify({ event: 'identified', userId: context.userId }));
      }
    } catch (e: any) {
      ws.send(JSON.stringify({ event: 'error', message: 'Invalid JSON payload' }));
    }
  });

  ws.on('close', () => {
    for (const room of context.subscriptions) {
      rooms.get(room)?.delete(ws);
      broadcastToRoom(room, {
        event: 'user_left',
        clientId: context.id,
        room
      });
    }
    clients.delete(ws);
  });
});

// Heartbeat ping interval to prune dead connections
const heartbeatInterval = setInterval(() => {
  for (const [ws, context] of clients.entries()) {
    if (!context.isAlive) {
      ws.terminate();
      clients.delete(ws);
      continue;
    }
    context.isAlive = false;
    ws.ping();
  }
}, 30000);

wss.on('close', () => {
  clearInterval(heartbeatInterval);
});

server.listen(PORT, () => {
  console.log(`[IntelliOps] Realtime WebSocket gateway listening on http://localhost:${PORT}`);
});
