package org.chatgpt;

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
    ORDER_PARTIALLY_FILLED
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

class MatchEngine {
    private Map<Integer, Order> orderMap;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;
    private MessageBus messageBus;

    public MatchEngine(MessageBus messageBus) {
        this.orderMap = new HashMap<>();
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
        this.messageBus = messageBus;
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
            order.setFilled(true);
            orderMap.remove(orderId);

            if (order.getSide() == Side.BUY) {
                buyOrders.remove(order);
            } else {
                sellOrders.remove(order);
            }

            messageBus.publish(new OrderFullyFilled(orderId));
        }
    }

    private void matchBuyOrder(Order buyOrder) {
        while (!sellOrders.isEmpty()) {
            Order sellOrder = sellOrders.peek();
            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                int filledQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                buyOrder.setQuantity(buyOrder.getQuantity() - filledQuantity);
                sellOrder.setQuantity(sellOrder.getQuantity() - filledQuantity);

                messageBus.publish(new TradeEvent(filledQuantity, sellOrder.getPrice()));
                messageBus.publish(new OrderPartiallyFilled(buyOrder.getOrderId(), filledQuantity, buyOrder.getQuantity()));
                messageBus.publish(new OrderPartiallyFilled(sellOrder.getOrderId(), filledQuantity, sellOrder.getQuantity()));

                if (sellOrder.getQuantity() == 0) {
                    sellOrders.poll();
                    orderMap.remove(sellOrder.getOrderId());
                    messageBus.publish(new OrderFullyFilled(sellOrder.getOrderId()));
                }

                if (buyOrder.getQuantity() == 0) {
                    break;
                }
            } else {
                break;
            }
        }

        if (buyOrder.getQuantity() == 0) {
            orderMap.remove(buyOrder.getOrderId());
            messageBus.publish(new OrderFullyFilled(buyOrder.getOrderId()));
        } else {
            messageBus.publish(new OrderPartiallyFilled(buyOrder.getOrderId(), buyOrder.getQuantity(), 0));
        }
    }

    private void matchSellOrder(Order sellOrder) {
        while (!buyOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            if (sellOrder.getPrice() <= buyOrder.getPrice()) {
                int filledQuantity = Math.min(sellOrder.getQuantity(), buyOrder.getQuantity());
                sellOrder.setQuantity(sellOrder.getQuantity() - filledQuantity);
                buyOrder.setQuantity(buyOrder.getQuantity() - filledQuantity);

                messageBus.publish(new TradeEvent(filledQuantity, sellOrder.getPrice()));
                messageBus.publish(new OrderPartiallyFilled(sellOrder.getOrderId(), filledQuantity, sellOrder.getQuantity()));
                messageBus.publish(new OrderPartiallyFilled(buyOrder.getOrderId(), filledQuantity, buyOrder.getQuantity()));

                if (buyOrder.getQuantity() == 0) {
                    buyOrders.poll();
                    orderMap.remove(buyOrder.getOrderId());
                    messageBus.publish(new OrderFullyFilled(buyOrder.getOrderId()));
                }

                if (sellOrder.getQuantity() == 0) {
                    break;
                }
            } else {
                break;
            }
        }

        if (sellOrder.getQuantity() == 0) {
            orderMap.remove(sellOrder.getOrderId());
            messageBus.publish(new OrderFullyFilled(sellOrder.getOrderId()));
        } else {
            messageBus.publish(new OrderPartiallyFilled(sellOrder.getOrderId(), sellOrder.getQuantity(), 0));
        }
    }

    private void showOrderBook() {
        System.out.println("--------------------    --------------------");
        System.out.println("Buy Orders:");
        PriorityQueue<Order> sortedBuyOrders = new PriorityQueue<>(buyOrders);
        while (!sortedBuyOrders.isEmpty()) {
            Order order = sortedBuyOrders.poll();
            System.out.println(order);
        }

        System.out.println("Sell Orders:");
        PriorityQueue<Order> sortedSellOrders = new PriorityQueue<>(sellOrders);
        while (!sortedSellOrders.isEmpty()) {
            Order order = sortedSellOrders.poll();
            System.out.println(order);
        }
        System.out.println("--------------------    --------------------");

    }
}

class Main {
    public static void main(String[] args) {
        MessageBus messageBus = new MessageBus();
        MatchEngine matchEngine = new MatchEngine(messageBus);

        // Example usage
        matchEngine.process(new AddOrderRequest(100000, Side.SELL, 1, 1075));
        matchEngine.process(new AddOrderRequest(100001, Side.BUY, 9, 1000));
        matchEngine.process(new AddOrderRequest(100002, Side.BUY, 30, 975));
        matchEngine.process(new AddOrderRequest(100003, Side.SELL, 10, 1050));
        matchEngine.process(new AddOrderRequest(100004, Side.BUY, 10, 950));
        matchEngine.process(new CancelOrderRequest(100004));
        matchEngine.process(new AddOrderRequest(100005, Side.SELL, 2, 1025));
        matchEngine.process(new AddOrderRequest(100006, Side.BUY, 1, 1000));
        matchEngine.process(new CancelOrderRequest(100004));
        matchEngine.process(new AddOrderRequest(100007, Side.SELL, 5, 1025));
        matchEngine.process(new AddOrderRequest(100008, Side.BUY, 3, 1050));
    }
}
