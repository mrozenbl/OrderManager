# OrderManager API Documentation

## Overview

The OrderManager is a comprehensive order matching engine implementation for trading systems. It provides a complete framework for handling buy/sell orders, order matching, trade execution, and event publishing. The system supports multiple order types including limit orders, market orders, and stop-loss orders.

## Package Structure

```
org.tildenpark
├── Enums (Side, MessageType, OrderType)
├── Message Classes (Order requests and events)
├── Core Components (MatchEngine, Order, MessageBus)
├── Message Generators and Parsers
├── Comparators for order sorting
└── Main Application and Test Utilities
```

---

## Core Enums

### Side

Represents the side of an order in trading.

```java
public enum Side {
    BUY,    // Buy order
    SELL    // Sell order
}
```

**Usage Example:**
```java
Side buyOrder = Side.BUY;
Side sellOrder = Side.SELL;
```

### MessageType

Defines the different types of messages supported by the system.

```java
public enum MessageType {
    ADD_ORDER_REQUEST,        // Request to add a new order
    CANCEL_ORDER_REQUEST,     // Request to cancel an existing order
    TRADE_EVENT,             // Published when a trade occurs
    ORDER_FULLY_FILLED,      // Published when an order is completely filled
    ORDER_PARTIALLY_FILLED,  // Published when an order is partially filled
    MARKET_ORDER_REQUEST,    // Request for a market order
    STOP_LOSS_ORDER_REQUEST, // Request for a stop-loss order
    INVALID_ORDER            // Invalid order message
}
```

### OrderType

Specifies the type of order being placed.

```java
public enum OrderType {
    LIMIT,      // Limit order with specific price
    MARKET,     // Market order executed at best available price
    STOP_LOSS   // Stop-loss order triggered at specific price
}
```

---

## Message Classes

### Message (Base Class)

Base class for all message types in the system.

#### Constructor
```java
public Message(MessageType messageType)
```

#### Public Methods
```java
public MessageType getMessageType()  // Returns the message type
public String toString()             // String representation
public int hashCode()               // Hash code for object comparison
```

**Usage Example:**
```java
Message message = new Message(MessageType.ADD_ORDER_REQUEST);
MessageType type = message.getMessageType();
```

### AddOrderRequest

Represents a request to add a limit order to the order book.

#### Constructor
```java
public AddOrderRequest(int orderId, Side side, int quantity, double price)
```

#### Public Methods
```java
public int getOrderId()     // Returns the unique order identifier
public Side getSide()       // Returns BUY or SELL
public int getQuantity()    // Returns the order quantity
public double getPrice()    // Returns the limit price
```

**Usage Example:**
```java
// Create a buy order for 100 shares at $50.25
AddOrderRequest buyOrder = new AddOrderRequest(12345, Side.BUY, 100, 50.25);
System.out.println("Order ID: " + buyOrder.getOrderId());
System.out.println("Side: " + buyOrder.getSide());
System.out.println("Quantity: " + buyOrder.getQuantity());
System.out.println("Price: $" + buyOrder.getPrice());
```

### MarketOrderRequest

Represents a request for a market order (executed at best available price).

#### Constructor
```java
public MarketOrderRequest(int orderId, Side side, int quantity)
```

#### Public Methods
```java
public int getOrderId()     // Returns the unique order identifier
public Side getSide()       // Returns BUY or SELL
public int getQuantity()    // Returns the order quantity
```

**Usage Example:**
```java
// Create a market sell order for 50 shares
MarketOrderRequest marketSell = new MarketOrderRequest(12346, Side.SELL, 50);
```

### StopLossOrderRequest

Represents a stop-loss order that triggers when price reaches a specific level.

#### Constructor
```java
public StopLossOrderRequest(int orderId, Side side, int quantity, double stopPrice)
```

#### Public Methods
```java
public int getOrderId()         // Returns the unique order identifier
public Side getSide()           // Returns BUY or SELL
public int getQuantity()        // Returns the order quantity
public double getStopPrice()    // Returns the stop trigger price
public OrderType getOrderType() // Returns STOP_LOSS
```

**Usage Example:**
```java
// Create a stop-loss sell order for 75 shares at $45.00
StopLossOrderRequest stopLoss = new StopLossOrderRequest(12347, Side.SELL, 75, 45.00);
```

### CancelOrderRequest

Represents a request to cancel an existing order.

#### Constructor
```java
public CancelOrderRequest(int orderId)
```

#### Public Methods
```java
public int getOrderId()     // Returns the order ID to cancel
```

**Usage Example:**
```java
// Cancel order with ID 12345
CancelOrderRequest cancelRequest = new CancelOrderRequest(12345);
```

### TradeEvent

Published when a trade is executed between matching orders.

#### Constructor
```java
public TradeEvent(int quantity, double price)
```

#### Public Methods
```java
public int getQuantity()    // Returns the traded quantity
public double getPrice()    // Returns the execution price
```

**Usage Example:**
```java
// Trade event: 100 shares traded at $50.25
TradeEvent trade = new TradeEvent(100, 50.25);
```

### OrderFullyFilled

Published when an order is completely filled.

#### Constructor
```java
public OrderFullyFilled(int orderId)
```

#### Public Methods
```java
public int getOrderId()     // Returns the filled order ID
```

### OrderPartiallyFilled

Published when an order is partially filled.

#### Constructor
```java
public OrderPartiallyFilled(int orderId, int filledQuantity, int remainingQuantity)
```

#### Public Methods
```java
public int getOrderId()             // Returns the order ID
public int getFilledQuantity()      // Returns quantity that was filled
public int getRemainingQuantity()   // Returns remaining unfilled quantity
```

**Usage Example:**
```java
// Order 12345: 30 shares filled, 70 remaining
OrderPartiallyFilled partialFill = new OrderPartiallyFilled(12345, 30, 70);
```

---

## Core Components

### Order

Represents an individual order in the system.

#### Constructors
```java
public Order(int orderId, Side side, int quantity, double price)
public Order(int orderId, Side side, int quantity, double price, OrderType orderType)
```

#### Public Methods
```java
public int getOrderId()             // Returns unique order identifier
public Side getSide()               // Returns BUY or SELL
public int getQuantity()            // Returns current quantity
public void setQuantity(int qty)    // Updates order quantity
public double getPrice()            // Returns order price
public OrderType getOrderType()     // Returns order type
public boolean equals(Object obj)   // Equality comparison
public int hashCode()              // Hash code generation
public String toString()           // String representation
```

**Usage Example:**
```java
// Create a limit buy order
Order limitOrder = new Order(12348, Side.BUY, 200, 49.75);

// Create a market sell order
Order marketOrder = new Order(12349, Side.SELL, 150, 0.0, OrderType.MARKET);

// Update quantity after partial fill
limitOrder.setQuantity(150);
```

### MatchEngine

The core order matching engine that processes orders and executes trades.

#### Constructor
```java
public MatchEngine(MessageBus messageBus)
```

#### Public Methods
```java
public void process(Message message)    // Processes incoming messages/orders
```

**Usage Example:**
```java
// Create message bus and match engine
MessageBus messageBus = new MessageBus();
MatchEngine engine = new MatchEngine(messageBus);

// Process various order types
AddOrderRequest buyOrder = new AddOrderRequest(1, Side.BUY, 100, 50.00);
AddOrderRequest sellOrder = new AddOrderRequest(2, Side.SELL, 50, 50.00);

engine.process(buyOrder);   // Adds buy order to book
engine.process(sellOrder);  // Matches with buy order, generates trades

// Process market order
MarketOrderRequest marketOrder = new MarketOrderRequest(3, Side.SELL, 25);
engine.process(marketOrder);

// Cancel an order
CancelOrderRequest cancel = new CancelOrderRequest(1);
engine.process(cancel);
```

#### Internal Processing Logic
- **ADD_ORDER_REQUEST**: Adds order to appropriate queue and attempts matching
- **CANCEL_ORDER_REQUEST**: Removes order from book if it exists
- **MARKET_ORDER_REQUEST**: Creates market order and executes at best available price
- **STOP_LOSS_ORDER_REQUEST**: Creates stop-loss order or converts to market order if triggered

### MessageBus

Event publishing system for trade events and order status updates.

#### Constructor
```java
public MessageBus()
```

#### Public Methods
```java
public void publish(Message message)      // Publishes a message/event
public List<Message> getMessages()        // Returns all published messages
public void clearMessages()               // Clears message history
```

**Usage Example:**
```java
MessageBus bus = new MessageBus();

// Publish events
bus.publish(new TradeEvent(100, 50.25));
bus.publish(new OrderFullyFilled(12345));

// Retrieve and process messages
List<Message> events = bus.getMessages();
for (Message event : events) {
    System.out.println("Event: " + event);
}

// Clear history
bus.clearMessages();
```

---

## Message Generators and Parsers

### OrderMessageParser

Singleton parser for converting string input to message objects.

#### Public Methods
```java
public static OrderMessageParser getInstance()                    // Gets singleton instance
public List<Message> parseMessages(String[] lines)               // Parses multiple lines
public Message parseMessageFromString(String line)               // Parses single line
public static String stripComments(String input)                 // Removes comments from input
```

**Usage Example:**
```java
OrderMessageParser parser = OrderMessageParser.getInstance();

// Parse single order string: "0,12345,0,100,50.25"
// Format: messageType,orderId,side,quantity,price
String orderLine = "0,12345,0,100,50.25";  // Add buy order
Message order = parser.parseMessageFromString(orderLine);

// Parse multiple orders
String[] orderLines = {
    "0,1,0,100,50.25",  // Add buy order
    "0,2,1,50,50.30",   // Add sell order
    "1,1"               // Cancel order 1
};
List<Message> orders = parser.parseMessages(orderLines);
```

#### Input Format
- **Message Type Codes:**
  - `0` = Add Limit Order (requires: orderId, side, quantity, price)
  - `1` = Cancel Order (requires: orderId)
  - `5` = Market Order (requires: orderId, side, quantity)
  - `6` = Stop Loss Order (requires: orderId, side, quantity, stopPrice)
- **Side Codes:** `0` = BUY, `1` = SELL

### FileMessageGenerator

Singleton generator for reading orders from files.

#### Public Methods
```java
public static FileMessageGenerator getInstance()                           // Gets singleton instance
public List<Message> generateMessagesFromFile(String filePath) throws IOException  // Reads orders from file
```

**Usage Example:**
```java
FileMessageGenerator generator = FileMessageGenerator.getInstance();

try {
    List<Message> ordersFromFile = generator.generateMessagesFromFile("orders.txt");
    System.out.println("Loaded " + ordersFromFile.size() + " orders from file");
} catch (IOException e) {
    System.err.println("Error reading file: " + e.getMessage());
}
```

### RandomMessageGenerator

Singleton generator for creating random test orders.

#### Public Methods
```java
public static RandomMessageGenerator getInstance()         // Gets singleton instance
public List<Message> generateRandomMessages(int count)    // Generates random orders
```

**Usage Example:**
```java
RandomMessageGenerator generator = RandomMessageGenerator.getInstance();

// Generate 50 random orders for testing
List<Message> randomOrders = generator.generateRandomMessages(50);

// Process orders through match engine
MatchEngine engine = new MatchEngine(new MessageBus());
for (Message order : randomOrders) {
    engine.process(order);
}
```

### OrderRequestMessageGenerator

Comprehensive message generator with multiple generation strategies.

#### Public Methods
```java
public static OrderRequestMessageGenerator getInstance()                      // Gets singleton instance
public List<Message> generateMessagesFromString(String messageString)        // Parses from string
public List<Message> generateRandomMessages(int count)                       // Generates random messages
public List<Message> readMessagesFromFile(String filePath) throws IOException // Reads from file
public List<Message> generateSequentialMessages(int count)                   // Generates sequential test orders
```

**Usage Example:**
```java
OrderRequestMessageGenerator generator = OrderRequestMessageGenerator.getInstance();

// Generate from multi-line string
String orderData = """
    0,1,0,100,50.25
    0,2,1,50,50.30
    1,1
    """;
List<Message> orders = generator.generateMessagesFromString(orderData);

// Generate sequential test orders
List<Message> testOrders = generator.generateSequentialMessages(20);

// Read from file
List<Message> fileOrders = generator.readMessagesFromFile("market_data.txt");
```

---

## Order Comparators

### BuyOrderComparator

Comparator for buy order priority queue (highest price first, then by order ID).

```java
public class BuyOrderComparator implements Comparator<Order>
```

#### Public Methods
```java
public int compare(Order o1, Order o2)  // Compares two buy orders
```

### SellOrderComparator

Comparator for sell order priority queue (lowest price first, then by order ID).

```java
public class SellOrderComparator implements Comparator<Order>
```

#### Public Methods
```java
public int compare(Order o1, Order o2)  // Compares two sell orders
```

**Usage Example:**
```java
// These are used internally by MatchEngine
PriorityQueue<Order> buyOrders = new PriorityQueue<>(new BuyOrderComparator());
PriorityQueue<Order> sellOrders = new PriorityQueue<>(new SellOrderComparator());
```

---

## Main Application

### Solution

Main application class with example usage and testing framework.

#### Public Methods
```java
public static void main(String[] args)  // Application entry point
```

**Usage Example:**
```java
// Run the complete order matching system
public class TradingApplication {
    public static void main(String[] args) {
        // Create components
        MessageBus messageBus = new MessageBus();
        MatchEngine matchEngine = new MatchEngine(messageBus);
        
        // Generate sample orders
        OrderRequestMessageGenerator generator = OrderRequestMessageGenerator.getInstance();
        List<Message> orders = generator.generateRandomMessages(100);
        
        // Process orders
        for (Message order : orders) {
            matchEngine.process(order);
        }
        
        // Review results
        List<Message> events = messageBus.getMessages();
        System.out.println("Total events generated: " + events.size());
    }
}
```

---

## Complete Usage Examples

### Basic Order Processing

```java
// Create system components
MessageBus messageBus = new MessageBus();
MatchEngine engine = new MatchEngine(messageBus);

// Create and process buy order
AddOrderRequest buyOrder = new AddOrderRequest(1, Side.BUY, 100, 50.00);
engine.process(buyOrder);

// Create and process matching sell order
AddOrderRequest sellOrder = new AddOrderRequest(2, Side.SELL, 100, 50.00);
engine.process(sellOrder);  // This will generate a trade

// Check generated events
List<Message> events = messageBus.getMessages();
for (Message event : events) {
    if (event instanceof TradeEvent) {
        TradeEvent trade = (TradeEvent) event;
        System.out.println("Trade: " + trade.getQuantity() + " @ $" + trade.getPrice());
    }
}
```

### Market Order Processing

```java
// Add some limit orders to the book
engine.process(new AddOrderRequest(1, Side.BUY, 100, 49.95));
engine.process(new AddOrderRequest(2, Side.SELL, 50, 50.05));

// Place market buy order (will execute against best sell)
MarketOrderRequest marketBuy = new MarketOrderRequest(3, Side.BUY, 25);
engine.process(marketBuy);
```

### Stop Loss Order

```java
// Place stop loss sell order
StopLossOrderRequest stopLoss = new StopLossOrderRequest(4, Side.SELL, 100, 48.00);
engine.process(stopLoss);

// If market price drops to or below $48.00, this becomes a market order
```

### File-Based Order Processing

```java
// Create orders.txt file with format:
// 0,1,0,100,50.25    (Add buy order)
// 0,2,1,50,50.30     (Add sell order)  
// 1,1                (Cancel order 1)

FileMessageGenerator fileGen = FileMessageGenerator.getInstance();
List<Message> orders = fileGen.generateMessagesFromFile("orders.txt");

for (Message order : orders) {
    engine.process(order);
}
```

### Event Handling

```java
MessageBus bus = new MessageBus();
MatchEngine engine = new MatchEngine(bus);

// Process some orders...
engine.process(buyOrder);
engine.process(sellOrder);

// Handle different event types
List<Message> events = bus.getMessages();
for (Message event : events) {
    switch (event.getMessageType()) {
        case TRADE_EVENT:
            TradeEvent trade = (TradeEvent) event;
            System.out.println("TRADE: " + trade.getQuantity() + " @ $" + trade.getPrice());
            break;
        case ORDER_FULLY_FILLED:
            OrderFullyFilled filled = (OrderFullyFilled) event;
            System.out.println("ORDER FILLED: " + filled.getOrderId());
            break;
        case ORDER_PARTIALLY_FILLED:
            OrderPartiallyFilled partial = (OrderPartiallyFilled) event;
            System.out.println("PARTIAL FILL: " + partial.getOrderId() + 
                             ", Filled: " + partial.getFilledQuantity() +
                             ", Remaining: " + partial.getRemainingQuantity());
            break;
    }
}
```

---

## Error Handling

The system includes robust error handling for various scenarios:

- **Invalid message formats**: Handled by `OrderMessageParser`
- **File I/O errors**: Handled by `FileMessageGenerator`
- **Invalid order data**: Generates `InvalidMessage` objects
- **Missing orders for cancellation**: Silently ignored
- **Numeric parsing errors**: Caught and logged

## Performance Considerations

- **Time Complexity**: Order matching is O(log n) for insertion and O(1) for best price lookup
- **Space Complexity**: O(n) where n is the number of active orders
- **Thread Safety**: The system is designed for single-threaded operation
- **Memory Management**: Uses efficient priority queues for order books

## Extension Points

The system is designed for easy extension:

1. **New Order Types**: Extend `Message` class and update `MatchEngine.process()`
2. **Custom Matching Logic**: Modify `MatchEngine.matchOrder()` method
3. **Additional Events**: Create new message types extending `Message`
4. **Custom Generators**: Implement new message generation strategies
5. **Persistence**: Add database integration to `MessageBus` or `MatchEngine`

This comprehensive API documentation covers all public interfaces, usage patterns, and extension points in the OrderManager system.