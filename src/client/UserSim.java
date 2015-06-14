package client;

import driver.MainAutomatedTest;
import exceptions.InvalidParameterException;
import exceptions.InvalidPriceOperation;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import prices.Price;
import prices.PriceFactory;
import trading.ProductService;

public class UserSim implements Runnable {

    private User user;
    private boolean running = true;
    private boolean showDisplay = false;
    private int waitBase = 1000; // ms
    private long runDuration = 60000; // 1 min
    private int orderCount = 0;
    private int orderCxlCount = 0;
    private int quoteCount = 0;
    private int quoteCxlCount = 0;
    private int bookDepthCount = 0;

    public UserSim(long time, User u, boolean show) {
        user = u;
        runDuration = time;
        showDisplay = show;
    }

    public String getUserName() {
        return user.getUserName();
    }

    public int getOrderCount() {
        return orderCount;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public int getOrderCxlCount() {
        return orderCxlCount;
    }

    public int getQuoteCxlCount() {
        return quoteCxlCount;
    }

    public int getBookDepthCount() {
        return bookDepthCount;
    }

    public Price getNetAccountValue() throws InvalidPriceOperation {
        return user.getNetAccountValue();
    }

    @Override
    public void run() {
        System.out.println("Simulated user '" + user.getUserName() + "' starting trading activity - " + runDuration / 1000 + " second duration.");
        long start = System.currentTimeMillis();
        try {
            user.connect();
            subscribeUser(user);
            if (showDisplay) {
                user.showMarketDisplay();
            }

            while (running) {
                try {
                    doRandomEvent();
                } catch (Exception ex) {
                    Logger.getLogger(UserSim.class.getName()).log(Level.SEVERE, null, ex);
                }

                waitRandomTime();

                if ((System.currentTimeMillis() - start) > runDuration) {
                    running = false;
                }
            }

            MainAutomatedTest.simDone();


        } catch (Exception ex) {
            Logger.getLogger(UserSim.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void subscribeUser(User u) {
        for (String s : ProductService.getInstance().getProductList()) {
            try {
                u.subscribeCurrentMarket(s);
                u.subscribeLastSale(s);
                u.subscribeMessages(s);
                u.subscribeTicker(s);
            } catch (Exception ex) {
                Logger.getLogger(MainAutomatedTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void doRandomEvent() throws InvalidParameterException, UserNotConnectedException, OrderNotFoundException {

        double num = Math.random();

        if (num < 0.70) // Quote
        {
            double next = Math.random();
            if (next < 0.85) {
                makeQuote();
            } else {
                makeQuoteCancel();
            }
        } else if (num < 0.9) // Order
        {
            double next = Math.random();
            if (next < 0.55) {
                makeOrder();

            } else {
                makeOrderCancel(); //OK
            }
        } else {
            makeBookDepth();
        }
    }

    private void makeBookDepth() throws InvalidParameterException, UserNotConnectedException {
        ArrayList<String> list = user.getProductList();
        String product = list.get((int) (Math.random() * list.size()));
        String[][] bd = user.getBookDepth(product);
        //printBookDepth(bd);
        bookDepthCount++;
    }

    private void makeOrderCancel() throws InvalidParameterException, OrderNotFoundException, UserNotConnectedException {
        ArrayList<TradableUserData> list = user.getOrderIds();
        if (list.isEmpty()) {
            return;
        }
        TradableUserData order = list.get((int) (Math.random() * list.size()));

        user.submitOrderCancel(order.getSymbol(), order.getSide(), order.getOrderId());
        orderCxlCount++;
    }

    private void makeOrder() throws InvalidParameterException, UserNotConnectedException  {
        ArrayList<String> list = user.getProductList();
        String product = list.get((int) (Math.random() * list.size()));
        String side = makeRandomSide(); // This should match your format for storing a side - enum, String, etc
        Price p = makeRandomOrderPrice(side, product);
        int v = makeRandomVolume(product);

        user.submitOrder(product, p, v, side);
        orderCount++;
    }

    private void makeQuoteCancel() throws InvalidParameterException, UserNotConnectedException {
        ArrayList<String> list = user.getProductList();
        String product = list.get((int) (Math.random() * list.size()));
        user.submitQuoteCancel(product);
        quoteCxlCount++;
    }

    private void makeQuote() {
        ArrayList<String> list = user.getProductList();
        String product = list.get((int) (Math.random() * list.size()));
        Price bp = makeRandomPrice("BUY", product); // This should match your format for storing a side - enum, String, etc
        int bv = makeRandomVolume(product);

        Price sp = makeRandomPrice("SELL", product); // This should match your format for storing a side - enum, String, etc
        int sv = makeRandomVolume(product);



        try {
            if (bp.greaterOrEqual(sp)) {
                bp = sp.subtract(PriceFactory.makeLimitPrice("0.01"));
            }
            user.submitQuote(product, bp, bv, sp, sv);
            quoteCount++;
        } catch (Exception ex) {
            Logger.getLogger(UserSim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String makeRandomSide() { // This should match your format for storing a side - enum, String, etc
        if (Math.random() < 0.5) {
            return "BUY"; // This should match your format for storing a side - enum, String, etc
        } else {
            return "SELL"; // This should match your format for storing a side - enum, String, etc
        } 
    }

    private Price makeRandomOrderPrice(String side, String product) { // This should match your format for storing a side - enum, String, etc

        if (Math.random() < 0.1) {
            return PriceFactory.makeMarketPrice();
        } else {
            return makeRandomPrice(side, product);
        }
    }

    private Price makeRandomPrice(String side, String product) { // This should match your format for storing a side - enum, String, etc

        double priceBase = (side == "BUY" ? UserSimSettings.getBuyPriceBase(product) : UserSimSettings.getSellPriceBase(product)); // This should match your format for storing a side - enum, String, etc

        double price = priceBase * (1 - UserSimSettings.priceVariance);
        price += priceBase * (UserSimSettings.priceVariance * 2) * Math.random();

        return PriceFactory.makeLimitPrice(String.format("%.2f", price));
    }

    private int makeRandomVolume(String product) {
        int vol = (int) (UserSimSettings.getVolumeBase(product) * (1 - UserSimSettings.volumeVariance));
        vol += UserSimSettings.getVolumeBase(product) * (UserSimSettings.volumeVariance * 2) * Math.random();
        return vol;
    }

    private void waitRandomTime() {
        int waitTime = (int) ((0.75 * waitBase) + (0.5 * waitBase * Math.random()));

        synchronized (this) {
            try {
                wait(waitTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserSim.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void printBookDepth(String[][] bd) {
        int max = Math.max(bd[0].length, bd[1].length);

        System.out.println("----------------------------");
        System.out.print(String.format("%-16s%-16s%n", "BUY", "SELL"));
        for (int i = 0; i < max; i++) {
            System.out.print(String.format("%-16s", bd[0].length > i ? bd[0][i] : ""));
            System.out.println(String.format("%-16s", bd[1].length > i ? bd[1][i] : ""));
        }
        System.out.println("----------------------------");
    }
}
