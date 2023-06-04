package org.tildenpark;
/**
 * This collection of classes represents am implementation match engine that handles
 * buy and sell orders for a single stock. The match engine is responsible for
 * maintaining the order book, matching orders, and publishing trade events.
 * <p>
 * The match engine is a single threaded application. It receives messages
 * from a single input source. The input source can be a file or generated input.
 * <p>
 * The match engine is responsible for maintaining the order book and
 * publishing trade events. The match engine is also responsible for
 * handling the following order types:
 *      <ul>
 *         <li>Limit Order</li>
 *         <li>Market Order</li>
 *         <li>Stop Loss Order</li>
 *         <li>Cancel Order</li>
 *      </ul>
 *
 * @author Michael Rozenblit
 * @version 1.0
 * @since 2023-06-04
 */

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
    STOP_LOSS_ORDER_REQUEST,
    INVALID_ORDER
}

enum OrderType {
    LIMIT,
    MARKET,
    STOP_LOSS
}

class MarketOrderRequest extends Message {
    private int orderId;
    private final Side side;
    private int quantity;

    public MarketOrderRequest(int orderId, Side side, int quantity) {
        super(MessageType.MARKET_ORDER_REQUEST);
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
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

    @Override
    public String toString() {
        return "MarketOrderRequest{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, side, quantity);
    }
}

class StopLossOrderRequest extends Message {
    private int orderId;
    private final Side side;
    private int quantity;
    private double stopPrice;

    OrderType orderType = OrderType.STOP_LOSS;

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

    public OrderType getOrderType() {
        return orderType;
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
    private final MessageType messageType;

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
    private final int orderId;
    private final Side side;
    private int quantity;
    private final double price;

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
    private final int orderId;

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
    private final double price;

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

class InvalidMessage extends Message {
    private int orderId;
    private Side side;
    private int quantity;
    private double price;

    public InvalidMessage() {
        super(MessageType.INVALID_ORDER);
        this.orderId = generateInvalidOrderId();
        this.side = generateInvalidSide();
        this.quantity = generateInvalidQuantity();
        this.price = generateInvalidPrice();
    }

    // Implement getters and setters for the fields

    private int generateInvalidOrderId() {
        // Generate a negative or zero orderId
        return -(int) (Math.random() * 100) - 1;
    }

    private Side generateInvalidSide() {
        // Randomly select a side that is not BUY or SELL
        Side[] sides = Side.values();
        int randomIndex = (int) (Math.random() * sides.length);
        return sides[randomIndex];
    }

    private int generateInvalidQuantity() {
        // Generate a negative or zero quantity
        return -(int) (Math.random() * 10) - 1;
    }

    private double generateInvalidPrice() {
        // Generate a negative or zero price
        return -(Math.random() * 100) - 1;
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

    public static String stripComments(String input) {
        // Remove // comments that follow text
        String regex = "(?s)(?<=\\S)\\s*//.*?(?=\\R|$)";
        return input.replaceAll(regex, "");
    }

    public Message parseMessageFromString(String line) {
        line = stripComments(line);
        String[] parts = line.split(ORDER_SEPARATOR);
        // show if line is not valid
        if (parts.length < 2) {
            System.out.println("ERROR: Invalid input: " + line);
        } else if (parts.length >= 2) {
            try {
                int messageType = Integer.parseInt(parts[0]);
                int orderId = Integer.parseInt(parts[1]);
                Message parsed = parseOrderMessage(messageType, orderId, parts);
                System.out.println("Parsed:" + parsed);
                return parsed;
            } catch (NumberFormatException exception) {
                System.err.println(exception.toString());
                exception.printStackTrace();
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
        } else if (messageType == 5) {
            if (parts.length >= 3) {
                Side side = Integer.parseInt(parts[2]) == 0 ? Side.BUY : Side.SELL;
                int quantity = Integer.parseInt(parts[3]);

                return new MarketOrderRequest(orderId, side, quantity);
            }
        } else if (messageType == 6) {
            if (parts.length >= 5) {
                Side side = Integer.parseInt(parts[2]) == 0 ? Side.BUY : Side.SELL;
                int quantity = Integer.parseInt(parts[3]);
                double stopPrice = Double.parseDouble(parts[4]);

                return new StopLossOrderRequest(orderId, side, quantity, stopPrice);
            }
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
        double messageType = Math.random() * 5;
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
        } else if (messageType < 2) {
            if (addedOrders.isEmpty()) {
                return generateRandomMessage();
            }

            int randomIndex = (int) (Math.random() * addedOrders.size());
            AddOrderRequest addOrderRequest = addedOrders.get(randomIndex);
            addedOrders.remove(randomIndex);

            orderId = addOrderRequest.getOrderId();
            return new CancelOrderRequest(orderId);
        } else if (messageType < 3) {
            orderId = generateUniqueId();
            side = Math.random() < 0.5 ? Side.BUY : Side.SELL;
            quantity = (int) (Math.random() * 10) + 1;

            return new MarketOrderRequest(orderId, side, quantity);
        } else if (messageType < 4) {
            orderId = generateUniqueId();
            side = Math.random() < 0.5 ? Side.BUY : Side.SELL;
            quantity = (int) (Math.random() * 10) + 1;
            price = generateRandomPrice();

            return new StopLossOrderRequest(orderId, side, quantity, price);
        } else {
            return new InvalidMessage();
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
    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;
    private final MessageBus messageBus;
    private Map<Integer, Order> orderMap;

    public MatchEngine(MessageBus messageBus) {
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
        this.messageBus = messageBus;
        this.orderMap = new HashMap<>();
    }

    private void removeZeroQuantityOrder(Order order) {
        boolean isBuyOrder = order.getSide() == Side.BUY;
        PriorityQueue<Order> orders = isBuyOrder ? buyOrders : sellOrders;
        orders.remove(order);
        orderMap.remove(order.getOrderId());
    }

    public void process(Message message) {
        switch (message.getMessageType()) {
            case ADD_ORDER_REQUEST -> processAddOrder((AddOrderRequest) message);
            case CANCEL_ORDER_REQUEST -> processCancelOrder((CancelOrderRequest) message);
            case MARKET_ORDER_REQUEST -> processMarketOrder((MarketOrderRequest) message);
            case STOP_LOSS_ORDER_REQUEST -> processStopLossOrder((StopLossOrderRequest) message);
            default -> System.out.println("Invalid message type: " + message.getMessageType());
        }
        showOrderBook();
    }

    private void processAddOrder(AddOrderRequest request) {
        double price = request.getPrice();
        Order order = new Order(request.getOrderId(), request.getSide(), request.getQuantity(), price);
        orderMap.put(order.getOrderId(), order);

        if (order.getSide() == Side.BUY) {
            buyOrders.add(order);
            matchOrder(order, sellOrders, true);
        } else {
            sellOrders.add(order);
            matchOrder(order, buyOrders, false);
        }
    }

    private void processCancelOrder(CancelOrderRequest request) {
        int orderId = request.getOrderId();
        Order order = orderMap.get(orderId);

        if (order != null) {
            orderMap.remove(orderId);
            boolean isBuyOrder = order.getSide() == Side.BUY;
            PriorityQueue<Order> orders = isBuyOrder ? buyOrders : sellOrders;
            orders.remove(order);
            messageBus.publish(new CancelOrder(orderId));
        }
    }

    private void processMarketOrder(MarketOrderRequest request) {
        Side side = request.getSide();
        int quantity = request.getQuantity();
        double price = (side == Side.BUY) ? getBestAskPrice() : getBestBidPrice();
        Order order = new Order(request.getOrderId(), side, quantity, price, OrderType.MARKET);
        orderMap.put(order.getOrderId(), order);

        if (side == Side.BUY) {
            buyOrders.add(order);
            matchOrder(order, sellOrders, true);
        } else {
            sellOrders.add(order);
            matchOrder(order, buyOrders, false);
        }
    }

    private void processStopLossOrder(StopLossOrderRequest request) {
        int orderId = request.getOrderId();
        Side side = request.getSide();
        int quantity = request.getQuantity();
        double stopPrice = request.getStopPrice();
        double currentPrice = (side == Side.BUY) ? getBestAskPrice() : getBestBidPrice();

        if ((side == Side.BUY && stopPrice <= currentPrice) || (side == Side.SELL && stopPrice >= currentPrice)) {
            // Stop price has been reached, convert to a market order
            processMarketOrder(new MarketOrderRequest(orderId, side, quantity));
        } else {
            Order order = new Order(orderId, side, quantity, stopPrice);
            orderMap.put(order.getOrderId(), order);

            if (side == Side.BUY) {
                buyOrders.add(order);
                matchOrder(order, sellOrders, true);
            } else {
                sellOrders.add(order);
                matchOrder(order, buyOrders, false);
            }
        }
    }

    private void matchOrder(Order order, PriorityQueue<Order> oppositeOrders, boolean isBuyOrder) {
        List<Order> matchedOrders = new ArrayList<>();

        boolean isMarketOrder = (order.getOrderType() == OrderType.MARKET);

        while (!oppositeOrders.isEmpty() && order.getQuantity() > 0) {
            Order oppositeOrder = oppositeOrders.peek();

            if (!isMarketOrder && ((isBuyOrder && oppositeOrder.getPrice() > order.getPrice()) ||
                    (!isBuyOrder && oppositeOrder.getPrice() < order.getPrice()))) {
                break;
            }

            if (oppositeOrder.getQuantity() <= order.getQuantity()) {
                oppositeOrders.poll();
                int tradeQuantity = oppositeOrder.getQuantity();
                messageBus.publish(new OrderFullyFilled(oppositeOrder.getOrderId()));
                messageBus.publish(new TradeEvent(tradeQuantity, oppositeOrder.getPrice()));
                order.setQuantity(order.getQuantity() - tradeQuantity);

                if (order.getQuantity() == 0) {
                    messageBus.publish(new OrderFullyFilled(order.getOrderId()));
                    messageBus.publish(new TradeEvent(tradeQuantity, order.getPrice()));
                    removeZeroQuantityOrder(order);
                    return;
                } else {
                    messageBus.publish(new OrderPartiallyFilled(order.getOrderId(), tradeQuantity, order.getQuantity()));
                }

                matchedOrders.add(oppositeOrder);
            } else {
                int tradeQuantity = order.getQuantity();
                oppositeOrder.setQuantity(oppositeOrder.getQuantity() - tradeQuantity);
                messageBus.publish(new OrderPartiallyFilled(oppositeOrder.getOrderId(), tradeQuantity, oppositeOrder.getQuantity()));
                messageBus.publish(new TradeEvent(tradeQuantity, order.getPrice()));
                removeZeroQuantityOrder(order);

                if (order.getOrderType() != OrderType.MARKET)
                    return;
            }
        }

        for (Order matchedOrder : matchedOrders) {
            oppositeOrders.remove(matchedOrder);
            orderMap.remove(matchedOrder.getOrderId());
        }
    }

    private double getBestAskPrice() {
        if (!sellOrders.isEmpty()) {
            return sellOrders.peek().getPrice();
        }
        return 0.0;
    }

    private double getBestBidPrice() {
        if (!buyOrders.isEmpty()) {
            return buyOrders.peek().getPrice();
        }
        return 0.0;
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

    private int generateOrderId() {
        // Generate a unique order ID
        return orderMap.size() + 1;
    }
}

class Order {
    private final int orderId;
    private final Side side;
    private int quantity;
    private final double price;
    private final OrderType orderType;

    public Order(int orderId, Side side, int quantity, double price) {
        this(orderId, side, quantity, price, OrderType.LIMIT);
    }

    public Order(int orderId, Side side, int quantity, double price, OrderType orderType) {
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
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

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", side=" + side +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderType=" + orderType +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, side, quantity, price, orderType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Order other = (Order) obj;
        return orderId == other.orderId &&
                side == other.side &&
                quantity == other.quantity &&
                Double.compare(price, other.price) == 0 &&
                orderType == other.orderType;
    }



}

public class Solution {

    public static void main(String[] args) {
        try {
            // Create a MessageBus
            MessageBus messageBus = new MessageBus();

            // Create a MatchEngine
            MatchEngine matchEngine = new MatchEngine(messageBus);

            // Generate a sequence of order input messages
            List<Message> messages = null;
            try {
                messages = OrderRequestMessageGenerator.getInstance().generateMessagesFromString(OrderStringTest.getOrderString());
                //messages = RandomMessageGenerator.getInstance().generateRandomMessages(100);
                //messages = FileMessageGenerator.getInstance().generateMessagesFromFile("input.txt");
            } catch (Exception e) {
                System.err.println("Error generating messages: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            System.out.println("***** Input Messages: *****");
            showMessages(messages);
            System.out.println("***************************");

            // Process the order input messages
            for (Message message : messages) {
                System.out.println(" ==> " + message);
                try {
                    matchEngine.process(message);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + message + ", Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Verify the emitted output messages
            List<Message> outputMessages = messageBus.getMessages();
            List<Message> expectedOutputMessages = null;
            try {
                expectedOutputMessages = OrderStringTest.getExpectedOutput();
            } catch (Exception e) {
                System.err.println("Error generating expected output messages: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            try {
                verifyOutputMessages(outputMessages, expectedOutputMessages);
            } catch (Exception e) {
                System.err.println("Error verifying output messages: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private static void showMessages(List<Message> messageList) {
        int i = 0;
        for (Message message : messageList) {
            i++;
            System.out.println(i + " : " + message);
        }
    }
    private static void verifyOutputMessages(List<Message> outputMessages, List<Message> expectedOutputMessages) {
        // Define the expected output messages
        System.out.println("==> Verifying Output messages");
        showMessages(outputMessages);


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

class OrderStringTest {
    public static String getOrderString() {
        // message type, order id, side, quantity, price
        //  0 - add (limit) order request
        //  1 - cancel order request
        // OUT 2 - trade event
        // OUT 3 - order fully filled
        // OUT 4 - order partially filled
        //  5 - market order
        //  6 - stop loss order


        String orderString ="""
0,100000,1,1,1075
0,100001,0,9,1000
0,100002,0,30,975
0,100003,1,10,1050
0,100004,0,10,950
BADMESSAGE
0,100005,1,2,1025
0,100006,0,1,1000
1,100004
0,100007,1,5,1025
0,100008,0,3,1050    /// buy 3 @ 1050
5,100009,1,3        /// sell 3 market order
5,100010,0,10       /// buy 10 market order
6,100011,1,30,1000   /// stop loss order @ 1000
""" ;

        return orderString;

    }

    public static List<Message> getExpectedOutput() {
        List<Message> expectedOutputMessages = new ArrayList<>();
        expectedOutputMessages.add(new CancelOrder(100004));
        expectedOutputMessages.add(new OrderFullyFilled(100005));
        expectedOutputMessages.add(new TradeEvent(2, 1025.0));
        expectedOutputMessages.add(new OrderPartiallyFilled(100008, 2, 1));
        expectedOutputMessages.add(new OrderPartiallyFilled(100007, 1, 4));
        expectedOutputMessages.add(new TradeEvent(1, 1050.0));
        expectedOutputMessages.add(new OrderPartiallyFilled(100002, 3, 27));
        expectedOutputMessages.add(new TradeEvent(3, 975.0));
        expectedOutputMessages.add(new OrderFullyFilled(100007));
        expectedOutputMessages.add(new TradeEvent(4, 1025.0));
        expectedOutputMessages.add(new OrderPartiallyFilled(100010, 4, 6));
        expectedOutputMessages.add(new OrderPartiallyFilled(100003, 6, 4));
        expectedOutputMessages.add(new TradeEvent(6, 1025.0));
        expectedOutputMessages.add(new OrderFullyFilled(100002));
        expectedOutputMessages.add(new TradeEvent(27, 975.0));
        expectedOutputMessages.add(new OrderPartiallyFilled(100011, 27, 3));
        expectedOutputMessages.add(new OrderPartiallyFilled(100001, 3, 6));
        expectedOutputMessages.add(new TradeEvent(3, 975.0));

        return expectedOutputMessages;
    }

}
