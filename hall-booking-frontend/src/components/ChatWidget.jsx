import React, { useState } from 'react';
import { MessageCircle, X } from 'lucide-react';

const ChatWidget = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [chatHistory, setChatHistory] = useState([]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const userMessage = { sender: 'user', text: message };
    const botReply = generateBotReply(message);

    setChatHistory((prev) => [...prev, userMessage, { sender: 'bot', text: botReply }]);
    setMessage('');
  };

  const generateBotReply = (input) => {
    const msg = input.toLowerCase();

    if (msg.includes('hello') || msg.includes('hi')) return 'Hello! How can I help you today?';
    if (msg.includes('register')) return 'You can register as a vendor or customer from the "Register" button on the top.';
    if (msg.includes('login')) return 'You can log in using the "Customer Login" or "Vendor Login" options.';
    if (msg.includes('review')) return 'Click "Write a Review" on the top to leave your feedback!';
    if (msg.includes('booking')) return 'You can browse halls and book from the "Popular" or "Venues" section.';
    
    return "Sorry, I didn't understand that. Please try asking something else!";
  };

  return (
    <div className="fixed bottom-5 left-5 z-50">
      {/* Floating Chat Button */}
      {!open && (
        <button
          onClick={() => setOpen(true)}
          className="bg-pink-500 hover:bg-pink-600 text-white p-4 rounded-full shadow-lg transition-all"
        >
          <MessageCircle className="w-4 h-4" />
        </button>
      )}

      {/* Chat Box */}
      {open && (
        <div className="w-80 bg-white shadow-xl rounded-lg p-4">
          <div className="flex justify-between items-center mb-2">
            <h3 className="font-bold text-lg">Customer Support</h3>
            <button onClick={() => setOpen(false)}>
              <X className="w-5 h-5 text-gray-500" />
            </button>
          </div>

          <div className="h-60 overflow-y-auto mb-2 space-y-2">
            {chatHistory.map((msg, idx) => (
              <div
                key={idx}
                className={`text-sm p-2 rounded max-w-xs ${
                  msg.sender === 'user'
                    ? 'bg-pink-100 self-end ml-auto text-right'
                    : 'bg-gray-100 self-start text-left'
                }`}
              >
                <span>{msg.text}</span>
              </div>
            ))}
          </div>

          <form onSubmit={handleSubmit} className="space-y-2">
            <textarea
              rows="2"
              className="w-full border border-gray-300 rounded p-2 text-sm"
              placeholder="Type your message..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              required
            />
            <button
              type="submit"
              className="w-full bg-pink-500 text-white rounded py-2 hover:bg-red-600 text-sm"
            >
              Send
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default ChatWidget;
