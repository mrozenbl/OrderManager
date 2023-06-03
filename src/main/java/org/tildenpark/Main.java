package org.tildenpark;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

enum Side {
    BUY,
    SELL
}

enum MessageType {
    ADD_ORDER_REQUEST,
    CANCEL_ORDER_REQUEST,
    TRADE_EVENT,
    ORDER_FULLY_FILLED,
    ORDER_PARTIALLY_FILLED,
    MARKET_ORDER_REQUEST,
    STOP_LOSS_ORDER_REQUEST
}

class MarketOrderRequest extends Message {
    private Side side;
    private int quantity;

    public MarketOrderRequest(Side side, int quantity) {
        super(MessageType.MARKET_ORDER_REQUEST);
        this.side = side;
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "MarketOrderRequest{" +
                "side=" + side +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), side, quantity);
    }
}

class StopLossOrderRequest extends Message {
    private int orderId;
    private Side side;
    private int quantity;
    private double stopPrice;

    public StopLossOrderRequest(int orderId, Side side, int quantity, double stopPrice) {
        super(MessageType.STOP_LOSS_ORDER_REQUEST);
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.stopPrice = stopPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getStopPrice() {
        return stopPrice;
    }

    @Override
    public String toString() {
        return "StopLossOrderRequest{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", quantity=" + quantity +
                ", stopPrice=" + stopPrice +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, side, quantity, stopPrice);
    }
}




interface OrderMessage {
    MessageType getMessageType();
}

class Message implements OrderMessage {
    private MessageType messageType;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageType);
    }
}

class AddOrderRequest extends Message {
    private int orderId;
    private Side side;
    private int quantity;
    private double price;

    public AddOrderRequest(int orderId, Side side, int quantity, double price) {
        super(MessageType.ADD_ORDER_REQUEST);
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "AddOrderRequest{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, side, quantity, price);
    }
}

class CancelOrderRequest extends Message {
    private int orderId;

    public CancelOrderRequest(int orderId) {
        super(MessageType.CANCEL_ORDER_REQUEST);
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "CancelOrderRequest{" +
                "orderId=" + orderId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId);
    }
}

class TradeEvent extends Message {
    private int quantity;
    private double price;

    public TradeEvent(int quantity, double price) {
        super(MessageType.TRADE_EVENT);
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "TradeEvent{" +
                "quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quantity, price);
    }
}

class OrderFullyFilled extends Message {
    private int orderId;

    public OrderFullyFilled(int orderId) {
        super(MessageType.ORDER_FULLY_FILLED);
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "OrderFullyFilled{" +
                "orderId=" + orderId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId);
    }
}

class OrderPartiallyFilled extends Message {
    private int orderId;
    private int filledQuantity;
    private int remainingQuantity;

    public OrderPartiallyFilled(int orderId, int filledQuantity, int remainingQuantity) {
        super(MessageType.ORDER_PARTIALLY_FILLED);
        this.orderId = orderId;
        this.filledQuantity = filledQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getFilledQuantity() {
        return filledQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    @Override
    public String toString() {
        return "OrderPartiallyFilled{" +
                "orderId=" + orderId +
                ", filledQuantity=" + filledQuantity +
                ", remainingQuantity=" + remainingQuantity +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, filledQuantity, remainingQuantity);
    }
}

class CancelOrder extends Message {
    private int orderId;

    public CancelOrder(int orderId) {
        super(MessageType.CANCEL_ORDER_REQUEST);
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "CancelOrder{" +
                "orderId=" + orderId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId);
    }
}




class MessageBus {
    private List<Message> messages;

    public MessageBus() {
        this.messages = new ArrayList<>();
    }

    public void publish(Message message) {
        messages.add(message);
        System.out.println(message.toString());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clearMessages() {
        messages.clear();
    }
}


class OrderMessageParser {
    private static final String ORDER_SEPARATOR = ",";

    private OrderMessageParser() {
    }

    public static OrderMessageParser getInstance() {
        return OrderMessageParserHolder.INSTANCE;
    }

    public List<Message> parseMessages(String[] lines) {
        List<Message> messages = new ArrayList<>();

        for (String line : lines) {
            Message message = parseMessageFromString(line);
            if (message != null) {
                messages.add(message);
            }
        }

        return messages;
    }

    public Message parseMessageFromString(String line) {
        String[] parts = line.split(ORDER_SEPARATOR);
        // show if line is not valid
        if (parts.length < 2) {
            System.out.println("ERROR: Invalid input: " + line);
        } else if (parts.length >= 2) {
            try {
                int messageType = Integer.parseInt(parts[0]);
                int orderId = Integer.parseInt(parts[1]);
                return parseOrderMessage(messageType, orderId, parts);
            } catch (NumberFormatException ignored) {
                System.out.println(ignored.toString());
            }
        }
        return null;
    }

    private Message parseOrderMessage(int messageType, int orderId, String[] parts) {
        if (messageType == 0) {
            if (parts.length >= 5) {
                Side side = Integer.parseInt(parts[2]) == 0 ? Side.BUY : Side.SELL;
                int quantity = Integer.parseInt(parts[3]);
                double price = Double.parseDouble(parts[4]);
                return new AddOrderRequest(orderId, side, quantity, price);
            }
        } else if (messageType == 1) {
            return new CancelOrderRequest(orderId);
        }
        return null;
    }

    private static class OrderMessageParserHolder {
        private static final OrderMessageParser INSTANCE = new OrderMessageParser();
    }
}

class FileMessageGenerator {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static FileMessageGenerator instance;

    private FileMessageGenerator() {
    }

    public static FileMessageGenerator getInstance() {
        if (instance == null) {
            synchronized (FileMessageGenerator.class) {
                if (instance == null) {
                    instance = new FileMessageGenerator();
                }
            }
        }
        return instance;
    }

    public List<Message> generateMessagesFromFile(String filePath) throws IOException {
        List<Message> messages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Message message = OrderMessageParser.getInstance().parseMessageFromString(line);
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        return messages;
    }
}



class RandomMessageGenerator {
    private static final double PRICE_THRESHOLD = 1e-8;
    private int currentOrderId = 1;
    private Set<Integer> usedOrderIds = new HashSet<>();
    private List<AddOrderRequest> addedOrders = new ArrayList<>();

    private static class RandomMessageGeneratorHolder {
        private static final RandomMessageGenerator INSTANCE = new RandomMessageGenerator();
    }

    private RandomMessageGenerator() {
    }

    public static RandomMessageGenerator getInstance() {
        return RandomMessageGeneratorHolder.INSTANCE;
    }

    public List<Message> generateRandomMessages(int count) {
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Message message = generateRandomMessage();
            messages.add(message);
        }

        return messages;
    }

    private Message generateRandomMessage() {
        double messageType = Math.random() * 2;
        int orderId;
        Side side;
        int quantity;
        double price;

        if (messageType < 1) {
            orderId = generateUniqueId();
            side = Math.random() < 0.5 ? Side.BUY : Side.SELL;
            quantity = (int) (Math.random() * 10) + 1;
            price = generateRandomPrice();

            AddOrderRequest addOrderRequest = new AddOrderRequest(orderId, side, quantity, price);
            addedOrders.add(addOrderRequest);

            return addOrderRequest;
        } else {
            if (addedOrders.isEmpty()) {
                return generateRandomMessage();
            }

            int randomIndex = (int) (Math.random() * addedOrders.size());
            AddOrderRequest addOrderRequest = addedOrders.get(randomIndex);
            addedOrders.remove(randomIndex);

            orderId = addOrderRequest.getOrderId();
            return new CancelOrderRequest(orderId);
        }
    }

    private int generateUniqueId() {
        int orderId = currentOrderId;
        do {
            orderId++;
        } while (usedOrderIds.contains(orderId));

        currentOrderId = orderId + 1;
        usedOrderIds.add(orderId);
        return orderId;
    }

    private double generateRandomPrice() {
        Random random = new Random();
        return random.nextInt(3) + 999;
    }
}

class OrderRequestMessageGenerator {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static OrderRequestMessageGenerator instance;

    private final List<Message> addedOrders;
    private int currentOrderId;

    private OrderRequestMessageGenerator() {
        addedOrders = new ArrayList<>();
        currentOrderId = 1;
    }

    public static OrderRequestMessageGenerator getInstance() {
        if (instance == null) {
            synchronized (OrderRequestMessageGenerator.class) {
                if (instance == null) {
                    instance = new OrderRequestMessageGenerator();
                }
            }
        }
        return instance;
    }

    public List<Message> generateMessagesFromString(String messageString) {
        String[] lines = messageString.trim().split(LINE_SEPARATOR);
        return OrderMessageParser.getInstance().parseMessages(lines);
    }

    public List<Message> generateRandomMessages(int count) {
        return RandomMessageGenerator.getInstance().generateRandomMessages(count);
    }

    public List<Message> readMessagesFromFile(String filePath) throws IOException {
        return FileMessageGenerator.getInstance().generateMessagesFromFile(filePath);
    }

    public List<Message> generateSequentialMessages(int count) {
        List<Message> messages = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Message message = generateSequentialMessage();
            messages.add(message);
        }

        return messages;
    }

    private Message generateSequentialMessage() {
        int messageType = (int) (Math.random() * 2);
        int orderId;
        if (messageType == 0) {
            orderId = getNextOrderId();
            Side side = Math.random() < 0.5 ? Side.BUY : Side.SELL;
            int quantity = (int) (Math.random() * 10) + 1;
            double price = Math.random() * 1000 + 900;
            AddOrderRequest addOrderRequest = new AddOrderRequest(orderId, side, quantity, price);
            addedOrders.add(addOrderRequest);
            return addOrderRequest;
        } else {
            if (addedOrders.isEmpty()) {
                return generateSequentialMessage();
            }
            int randomIndex = (int) (Math.random() * addedOrders.size());
            AddOrderRequest addOrderRequest = (AddOrderRequest) addedOrders.get(randomIndex);
            addedOrders.remove(randomIndex);
            return new CancelOrderRequest(addOrderRequest.getOrderId());
        }
    }

    private int getNextOrderId() {
        return currentOrderId++;
    }

    private static class OrderRequestMessageGeneratorHolder {
        private static final OrderRequestMessageGenerator INSTANCE = new OrderRequestMessageGenerator();
    }
}


class BuyOrderComparator implements Comparator<Order> {
    private static final double PRICE_THRESHOLD = 1e-8;

    @Override
    public int compare(Order o1, Order o2) {
        double price1 = o1.getPrice();
        double price2 = o2.getPrice();

        if (Math.abs(price1 - price2) > PRICE_THRESHOLD) {
            return Double.compare(price1, price2);
        } else {
            return Integer.compare(o1.getOrderId(), o2.getOrderId());
        }
    }
}

class SellOrderComparator implements Comparator<Order> {
    private static final double PRICE_THRESHOLD = 1e-8;

    @Override
    public int compare(Order o1, Order o2) {
        double price1 = o1.getPrice();
        double price2 = o2.getPrice();

        if (Math.abs(price1 - price2) > PRICE_THRESHOLD) {
            return Double.compare(price1, price2);
        } else {
            return Integer.compare(o1.getOrderId(), o2.getOrderId());
        }
    }
}


class MatchEngine {
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;
    private MessageBus messageBus;
    private Map<Integer, Order> orderMap;

    public MatchEngine(MessageBus messageBus) {
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
        this.messageBus = messageBus;
        this.orderMap = new HashMap<>();
    }

    public void process(Message message) {
        switch (message.getMessageType()) {
            case ADD_ORDER_REQUEST:
                processAddOrder((AddOrderRequest) message);
                break;
            case CANCEL_ORDER_REQUEST:
                processCancelOrder((CancelOrderRequest) message);
                break;
            default:
                System.out.println("Invalid message type: " + message.getMessageType());
        }
        showOrderBook();
    }

    private void processAddOrder(AddOrderRequest request) {
        double price = request.getPrice();
        Order order = new Order(request.getOrderId(), request.getSide(), request.getQuantity(), price);
        orderMap.put(order.getOrderId(), order);

        if (order.getSide() == Side.BUY) {
            buyOrders.add(order);
            matchBuyOrder(order);
        } else {
            sellOrders.add(order);
            matchSellOrder(order);
        }
    }

    private void processCancelOrder(CancelOrderRequest request) {
        int orderId = request.getOrderId();
        Order order = orderMap.get(orderId);

        if (order != null) {
            orderMap.remove(orderId);
            if (order.getSide() == Side.BUY) {
                buyOrders.remove(order);
            } else {
                sellOrders.remove(order);
            }
            messageBus.publish(new CancelOrder(orderId));
        }
    }

    private void matchBuyOrder(Order buyOrder) {
        List<Order> matchedOrders = new ArrayList<>();

        while (!sellOrders.isEmpty()) {
            Order sellOrder = sellOrders.peek();
            if (sellOrder.getPrice() > buyOrder.getPrice()) {
                break; // No more matches
            }

            if (sellOrder.getQuantity() <= buyOrder.getQuantity()) {
                sellOrders.poll();
                int tradeQuantity = sellOrder.getQuantity();
                messageBus.publish(new TradeEvent(tradeQuantity, sellOrder.getPrice()));
                messageBus.publish(new OrderFullyFilled(sellOrder.getOrderId()));
                messageBus.publish(new OrderFullyFilled(buyOrder.getOrderId()));
                buyOrder.setQuantity(buyOrder.getQuantity() - tradeQuantity);
                if (buyOrder.getQuantity() == 0) {
                    return; // Buy order fully filled
                }
                matchedOrders.add(sellOrder);
            } else {
                sellOrder.setQuantity(sellOrder.getQuantity() - buyOrder.getQuantity());
                messageBus.publish(new TradeEvent(buyOrder.getQuantity(), sellOrder.getPrice()));
                messageBus.publish(new OrderPartiallyFilled(sellOrder.getOrderId(), buyOrder.getQuantity(), sellOrder.getQuantity()));
                messageBus.publish(new OrderFullyFilled(buyOrder.getOrderId()));
                buyOrder.setQuantity(0);
                return; // Buy order fully filled
            }
        }

        for (Order matchedOrder : matchedOrders) {
            sellOrders.remove(matchedOrder);
            orderMap.remove(matchedOrder.getOrderId());
        }
    }

    private void matchSellOrder(Order sellOrder) {
        List<Order> matchedOrders = new ArrayList<>();

        while (!buyOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            if (buyOrder.getPrice() < sellOrder.getPrice()) {
                break; // No more matches
            }

            if (buyOrder.getQuantity() <= sellOrder.getQuantity()) {
                buyOrders.poll();
                int tradeQuantity = buyOrder.getQuantity();
                messageBus.publish(new TradeEvent(tradeQuantity, buyOrder.getPrice()));
                messageBus.publish(new OrderFullyFilled(buyOrder.getOrderId()));
                messageBus.publish(new OrderFullyFilled(sellOrder.getOrderId()));
                sellOrder.setQuantity(sellOrder.getQuantity() - tradeQuantity);
                if (sellOrder.getQuantity() == 0) {
                    return; // Sell order fully filled
                }
                matchedOrders.add(buyOrder);
            } else {
                buyOrder.setQuantity(buyOrder.getQuantity() - sellOrder.getQuantity());
                messageBus.publish(new TradeEvent(sellOrder.getQuantity(), buyOrder.getPrice()));
                messageBus.publish(new OrderPartiallyFilled(buyOrder.getOrderId(), sellOrder.getQuantity(), buyOrder.getQuantity()));
                messageBus.publish(new OrderFullyFilled(sellOrder.getOrderId()));
                sellOrder.setQuantity(0);
                return; // Sell order fully filled
            }
        }

        for (Order matchedOrder : matchedOrders) {
            buyOrders.remove(matchedOrder);
            orderMap.remove(matchedOrder.getOrderId());
        }
    }

    private void showOrderBook() {
        List<Order> sortedBuyOrders = new ArrayList<>(buyOrders);
        List<Order> sortedSellOrders = new ArrayList<>(sellOrders);

        Comparator<Order> buyComparator = (Comparator<Order>) buyOrders.comparator();
        Comparator<Order> sellComparator = (Comparator<Order>) sellOrders.comparator();

        sortedBuyOrders.sort(buyComparator);
        sortedSellOrders.sort(sellComparator);

        System.out.println("--- BUY ORDERS ---");
        for (Order order : sortedBuyOrders) {
            System.out.println(order);
        }

        System.out.println("--- SELL ORDERS ---");
        for (Order order : sortedSellOrders) {
            System.out.println(order);
        }

        System.out.println("------------------");
    }
}


class OrderStringTest {
    public static String  getOrderString() {
        String orderString = "0,100000,1,1,1075\n" +
                "0,100001,0,9,1000\n" +
                "0,100002,0,30,975\n" +
                "0,100003,1,10,1050\n" +
                "0,100004,0,10,950\n" +
                "BADMESSAGE\n" +
                "0,100005,1,2,1025\n" +
                "0,100006,0,1,1000\n" +
                "1,100004\n" +
                "0,100007,1,5,1025\n" +
                "0,100008,0,3,1050";

        return orderString;

    }

    public static List<Message> getExpectedOutput() {
        List<Message> expectedOutputMessages = new ArrayList<>();
        expectedOutputMessages.add(new CancelOrder(100004));
        expectedOutputMessages.add(new TradeEvent(2, 1025.0));
        expectedOutputMessages.add(new OrderFullyFilled(100005));
        expectedOutputMessages.add(new OrderFullyFilled(100008));
        expectedOutputMessages.add(new TradeEvent(1, 1025.0));
        expectedOutputMessages.add(new OrderPartiallyFilled(100007, 1, 4));
        expectedOutputMessages.add(new OrderFullyFilled(100008));

        return expectedOutputMessages;
    }

}

class Order {
    private final int orderId;
    private final Side side;
    private int quantity;
    private final double price;
    private boolean isFilled;

    public Order(int orderId, Side side, int quantity, double price) {
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.isFilled = false;
    }

    public int getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", quantity=" + quantity +
                ", price=" + price +
                ", isFilled=" + isFilled +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, side, quantity, price, isFilled);
    }
}



public class Main {
    public static void main(String[] args) {
        // Create a MessageBus
        MessageBus messageBus = new MessageBus();

        // Create a MatchEngine
        MatchEngine matchEngine = new MatchEngine(messageBus);

        // Generate a sequence of order input messages
        //List<Message> messages =  RandomMessageGenerator.getInstance().generateRandomMessages(10);
        List<Message> messages = OrderRequestMessageGenerator.getInstance().generateMessagesFromString(OrderStringTest.getOrderString());

        // Process the order input messages
        for (Message message : messages) {
            System.out.println(message);
            matchEngine.process(message);
        }

        // Verify the emitted output messages
        List<Message> outputMessages = messageBus.getMessages();
        List<Message> expectedOutputMessages = OrderStringTest.getExpectedOutput();
        verifyOutputMessages(outputMessages, expectedOutputMessages);

    }

    private static void showOutputMessages(List<Message> outputMessages) {
        for (Message message : outputMessages) {
            System.out.println(message);
        }
    }
    private static void verifyOutputMessages(List<Message> outputMessages, List<Message> expectedOutputMessages) {
        // Define the expected output messages
        showOutputMessages(outputMessages);


        // Check if the emitted output messages match the expected output messages
        if (outputMessages.size() != expectedOutputMessages.size()) {
            System.out.println("Output messages count mismatch!");
            return;
        }

        for (int i = 0; i < outputMessages.size(); i++) {
            Message outputMessage = outputMessages.get(i);
            Message expectedMessage = expectedOutputMessages.get(i);

            // Compare the output message with the expected message
            if (!outputMessage.toString().equals(expectedMessage.toString())) {
                System.out.println("Output message mismatch at index " + i);
                System.out.println("Expected: " + expectedMessage);
                System.out.println("Actual: " + outputMessage);
                return;
            }
        }

        System.out.println("Output messages verification passed!");
    }

}
