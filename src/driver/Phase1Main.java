
package driver;

import java.util.ArrayList;

import prices.Price;
import prices.PriceFactory;
import prices.InvalidPriceOperation;
import trading.Order;
import trading.Quote;
import trading.Tradable;
import trading.TradableDTO;

// HERE you should add any imports for your classes that you need to make this class compile.
// You will need imports for Price, PriceFactory, InvalidPriceOperation, Order, Quote, Tradable and TradableDTO;



public class Phase1Main {

    private static final ArrayList<Price> testPriceHolder = new ArrayList<>();

    public static void main(String[] args) {

        testPrices();
        testTradables();
    }

    
    private static void testTradables() {

        Tradable tradable1 = null;
        Quote quote1 = null;

        System.out.println("1) Create and print the content of a valid Order using Tradable reference:");
        try {
            tradable1 = new Order("USER1", "GE", PriceFactory.makeLimitPrice("$21.59"), 250, "BUY");
            System.out.println("Tradable's toString: " + tradable1 + "\n");
        } catch (Exception e) {
            System.out.println("An unexpected exception occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("2) Create and print the content of a TradableDTO:");
        TradableDTO tDTO = new TradableDTO(tradable1.getProduct(), tradable1.getPrice(), tradable1.getOriginalVolume(), tradable1.getRemainingVolume(),
                tradable1.getCancelledVolume(), tradable1.getUser(), tradable1.getSide(), tradable1.isQuote(), tradable1.getId());
        System.out.println("TradableDTO's toString: " + tDTO + "\n");

        
        System.out.println("3) Attempt to create an order using INVALID data (Zero volume) - should throw an exception:");
        try {
            Order order = new Order("USER1", "GE", PriceFactory.makeLimitPrice("$21.59"), 0, "BUY");
            System.out.println("*** If this prints then you have accepted invalid data in your Order - ERROR!\n\t" + order);
        } catch (Exception e) { // Catch anything you throw.
            // This block SHOULD execute.
            System.out.println("Properly handled an invalid volume -- error message is:\n\t" + e.getMessage() + "\n");
        }

        
        System.out.println("4) Change the cancelled and remaining volume of the order and display resulting tradable:");
        try {
            tradable1.setRemainingVolume(10);
            tradable1.setCancelledVolume(25);
            System.out.println("Tradable's toString: " + tradable1 + "\n");
        } catch (Exception e) {
            System.out.println("An unexpected exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        
        try {
            System.out.println("5) Change the Cancelled Volume of the order to greater than original volume - should throw an exception:");
            tradable1.setCancelledVolume(300);
        } catch (Exception e) { // Catch anything you throw.
            // This block SHOULD execute.
            System.out.println("Properly handled an invalid cancelled volume -- error message is:\n\t" + e.getMessage() + "\n");
        }

        
        try {
            System.out.println("6) Change the Remaining Volume of the order to greater than original volume - should throw an exception:");
            tradable1.setRemainingVolume(300);
        } catch (Exception e) { // Catch anything you throw.
            // This block SHOULD execute.
            System.out.println("Properly handled an invalid remaining volume -- error message is: \n\t" + e.getMessage() + "\n");
        }

        
        System.out.println("7) Create and print the content of a valid Quote:");
        try {
            quote1 = new Quote("USER2", "GE", PriceFactory.makeLimitPrice("$21.56"), 100, PriceFactory.makeLimitPrice("$21.62"), 100);
            System.out.println("Quote's toString: " + quote1 + "\n");
        } catch (Exception e) {
            System.out.println("An unexpected exception occurred: " + e.getMessage());
            e.printStackTrace();
        }

        
        System.out.println("8) Display the individual Quote Sides of the new Quote object:");
        System.out.println("\tQuoteSide's toString: " + quote1.getQuoteSide("BUY"));
        System.out.println("\tQuoteSide's toString: " + quote1.getQuoteSide("SELL") + "\n");

        
        try {
            System.out.println("9) Attempt to create a quote using INVALID data (negative sell volume) - should throw an exception:");
            quote1 = new Quote("USER2", "GE", PriceFactory.makeLimitPrice("$21.56"), 100, PriceFactory.makeLimitPrice("$21.62"), -50);
            System.out.println("If this prints then you have accepted invalid data in your Quote - ERROR!");
        } catch (Exception e) { // Catch anything you throw.
            System.out.println("Properly handled an invalid volume -- error message is: " + e.getMessage() + "\n");
        }

    }
    

    public static void testPrices() {
        makeSomeTestPriceObjects();
        verifyTestPriceValues();
        verifyMathematicalOperations();
        verifyBooleanChecks();
        verifyComparisons();
        verifyFlyweight();

        System.out.println("\nPrice Tests Complete\n\n");
    }

    private static void makeSomeTestPriceObjects() {
        System.out.println("1) Creating some Test Price Objects: ");
        testPriceHolder.add(PriceFactory.makeLimitPrice("10.50"));
        testPriceHolder.add(PriceFactory.makeLimitPrice("$1400.99"));
        testPriceHolder.add(PriceFactory.makeLimitPrice("$-51.52"));
        testPriceHolder.add(PriceFactory.makeLimitPrice(".49"));
        testPriceHolder.add(PriceFactory.makeLimitPrice("-0.89"));
        testPriceHolder.add(PriceFactory.makeLimitPrice("12"));
        testPriceHolder.add(PriceFactory.makeLimitPrice("90."));
        testPriceHolder.add(PriceFactory.makeLimitPrice("14.5"));
        testPriceHolder.add(PriceFactory.makeMarketPrice());
        System.out.println("   " + testPriceHolder);
        System.out.println();
    }

    private static void verifyTestPriceValues() {
        System.out.println("2) Verifying the Values Stored in Your Test Price Objects:");
        String format = "   %-11s --> %9s : %s%n";
        System.out.format(format, "\"$10.50\"", testPriceHolder.get(0), testPriceHolder.get(0).toString().equals("$10.50") ? "PASS" : "FAIL");
        System.out.format(format, "\"$1,400.99\"", testPriceHolder.get(1), testPriceHolder.get(1).toString().equals("$1,400.99") ? "PASS" : "FAIL");
        System.out.format(format, "\"$-51.52\"", testPriceHolder.get(2), testPriceHolder.get(2).toString().equals("$-51.52") ? "PASS" : "FAIL");
        System.out.format(format, "\"$0.49\"", testPriceHolder.get(3), testPriceHolder.get(3).toString().equals("$0.49") ? "PASS" : "FAIL");
        System.out.format(format, "\"$-0.89\"", testPriceHolder.get(4), testPriceHolder.get(4).toString().equals("$-0.89") ? "PASS" : "FAIL");
        System.out.format(format, "\"$12.00\"", testPriceHolder.get(5), testPriceHolder.get(5).toString().equals("$12.00") ? "PASS" : "FAIL");
        System.out.format(format, "\"$90.00\"", testPriceHolder.get(6), testPriceHolder.get(6).toString().equals("$90.00") ? "PASS" : "FAIL");
        System.out.format(format, "\"$14.50\"", testPriceHolder.get(7), testPriceHolder.get(7).toString().equals("$14.50") ? "PASS" : "FAIL");
        System.out.format(format, "\"MKT\"", testPriceHolder.get(8), testPriceHolder.get(8).toString().equals("MKT") ? "PASS" : "FAIL");
        System.out.println();
    }

    private static void verifyMathematicalOperations() {
        System.out.println("3) Verifying the Functionality of your Mathematical Operations:");
        String format = "   %-9s %c %9s = %9s : %s%n";
        try {
            Price results = testPriceHolder.get(0).add(testPriceHolder.get(1));
            System.out.format(format, testPriceHolder.get(0), '+', testPriceHolder.get(1), results, results.toString().equals("$1,411.49") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            Price results = testPriceHolder.get(1).subtract(testPriceHolder.get(1));
            System.out.format(format, testPriceHolder.get(1), '-', testPriceHolder.get(1), results, results.toString().equals("$0.00") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            Price results = testPriceHolder.get(2).add(testPriceHolder.get(3));
            System.out.format(format, testPriceHolder.get(2), '+', testPriceHolder.get(3), results, results.toString().equals("$-51.03") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            Price results = testPriceHolder.get(3).multiply(4);
            System.out.format(format, testPriceHolder.get(3), '*', 4, results, results.toString().equals("$1.96") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            Price results = testPriceHolder.get(4).subtract(testPriceHolder.get(5));
            System.out.format(format, testPriceHolder.get(4), '-', testPriceHolder.get(5), results, results.toString().equals("$-12.89") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            Price results = testPriceHolder.get(5).add(testPriceHolder.get(6));
            System.out.format(format, testPriceHolder.get(5), '+', testPriceHolder.get(6), results, results.toString().equals("$102.00") ? "PASS" : "FAIL");
        } catch (InvalidPriceOperation ex) {
            System.out.println("FAILED: " + ex.getMessage());
        }
        try {
            testPriceHolder.get(8).add(testPriceHolder.get(0));
            System.out.println("   FAIL: Adding a LIMIT price to a MARKET Price succeeded: (" + testPriceHolder.get(8) + " + " + testPriceHolder.get(0) + ")");
        } catch (InvalidPriceOperation ex) {
            System.out.println("   PASS: " + ex.getMessage() + ": (" + testPriceHolder.get(8) + " + " + testPriceHolder.get(0) + ")");
        }
        try {
            testPriceHolder.get(8).subtract(testPriceHolder.get(0));
            System.out.println("   FAIL: Subtracting a LIMIT price from a MARKET Price succeeded: (" + testPriceHolder.get(8) + " - " + testPriceHolder.get(0) + ")");
        } catch (InvalidPriceOperation ex) {
            System.out.println("   PASS: " + ex.getMessage() + ": (" + testPriceHolder.get(8) + " - " + testPriceHolder.get(0) + ")");
        }
        try {
            testPriceHolder.get(8).multiply(10);
            System.out.println("   FAIL: Multiplying a MARKET price succeeded: (" + testPriceHolder.get(8) + " + 10)");
        } catch (InvalidPriceOperation ex) {
            System.out.println("   PASS: " + ex.getMessage() + ": (" + testPriceHolder.get(8) + " * 10)");
        }
        System.out.println();
    }

    private static void verifyBooleanChecks() {
        System.out.println("4) Verifying the Functionality of your Boolean Checks:");
        System.out.println("   Value      | Negative Check |  MKT Check");
        System.out.println("   -------------------------------------------");
        String format = "   %-9s  | %-15s| %-12s%n";
        System.out.format(format, testPriceHolder.get(0), testPriceHolder.get(0).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(0).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(1), testPriceHolder.get(1).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(1).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(2), testPriceHolder.get(2).isNegative() ? "    PASS" : "    FAIL", testPriceHolder.get(2).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(3), testPriceHolder.get(3).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(3).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(4), testPriceHolder.get(4).isNegative() ? "    PASS" : "    FAIL", testPriceHolder.get(4).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(5), testPriceHolder.get(5).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(5).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(6), testPriceHolder.get(6).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(6).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(7), testPriceHolder.get(7).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(7).isMarket() ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(8), testPriceHolder.get(8).isNegative() ? "    FAIL" : "    PASS", testPriceHolder.get(8).isMarket() ? "    PASS" : "    FAIL");
        System.out.println();
    }

    private static void verifyComparisons() {
        System.out.println("5) Verifying the Functionality of your Boolean Comparisons:");
        Price testPrice = testPriceHolder.get(7);

        System.out.println("   (Comparison to " + testPrice + ")");

        String format = "    %-10s | %-15s | %-12s | %-12s | %-9s%n";
        System.out.println("    Value      | greaterOrEqual  | greaterThan  | lessOrEqual  | lessThan");
        System.out.println("    -------------------------------------------------------------------------");
        System.out.format(format, testPriceHolder.get(0),
                testPriceHolder.get(0).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(0).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(0).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(0).lessThan(testPrice) ? "    PASS" : "    FAIL");
        System.out.format(format, testPriceHolder.get(1),
                testPriceHolder.get(1).greaterOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(1).greaterThan(testPrice) ? "    PASS" : "    FAIL",
                testPriceHolder.get(1).lessOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(1).lessThan(testPrice) ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(2),
                testPriceHolder.get(2).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(2).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(2).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(2).lessThan(testPrice) ? "    PASS" : "    FAIL");
        System.out.format(format, testPriceHolder.get(3),
                testPriceHolder.get(3).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(3).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(3).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(3).lessThan(testPrice) ? "    PASS" : "    FAIL");
        System.out.format(format, testPriceHolder.get(4),
                testPriceHolder.get(4).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(4).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(4).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(4).lessThan(testPrice) ? "    PASS" : "    FAIL");
        System.out.format(format, testPriceHolder.get(5),
                testPriceHolder.get(5).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(5).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(5).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(5).lessThan(testPrice) ? "    PASS" : "    FAIL");
        System.out.format(format, testPriceHolder.get(6),
                testPriceHolder.get(6).greaterOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(6).greaterThan(testPrice) ? "    PASS" : "    FAIL",
                testPriceHolder.get(6).lessOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(6).lessThan(testPrice) ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(7),
                testPriceHolder.get(7).greaterOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(7).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(7).lessOrEqual(testPrice) ? "    PASS" : "    FAIL", testPriceHolder.get(7).lessThan(testPrice) ? "    FAIL" : "    PASS");
        System.out.format(format, testPriceHolder.get(8),
                testPriceHolder.get(8).greaterOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(8).greaterThan(testPrice) ? "    FAIL" : "    PASS",
                testPriceHolder.get(8).lessOrEqual(testPrice) ? "    FAIL" : "    PASS", testPriceHolder.get(8).lessThan(testPrice) ? "    FAIL" : "    PASS");
        System.out.println();

    }

    private static void verifyFlyweight() {
        System.out.println("6) Verifying your Flyweight Implementation:");
        String format = "    Price %-9s is same object as new %9s: %s%n";
        Price p1 = PriceFactory.makeLimitPrice("10.50");
        System.out.format(format, testPriceHolder.get(0), p1, testPriceHolder.get(0) == p1 ? "PASS" : "FAIL");
        System.out.format(format, testPriceHolder.get(1), p1, testPriceHolder.get(1) == p1 ? "FAIL" : "PASS");

        p1 = PriceFactory.makeMarketPrice();
        System.out.format(format, testPriceHolder.get(8), p1, testPriceHolder.get(8) == p1 ? "PASS" : "FAIL");
        System.out.format(format, testPriceHolder.get(1), p1, testPriceHolder.get(1) == p1 ? "FAIL" : "PASS");
    }
}

/*
Expected output:

1) Creating some Test Price Objects: 
   [$10.50, $1,400.99, $-51.52, $0.49, $-0.89, $12.00, $90.00, $14.50, MKT]

2) Verifying the Values Stored in Your Test Price Objects:
   "$10.50"    -->    $10.50 : PASS
   "$1,400.99" --> $1,400.99 : PASS
   "$-51.52"   -->   $-51.52 : PASS
   "$0.49"     -->     $0.49 : PASS
   "$-0.89"    -->    $-0.89 : PASS
   "$12.00"    -->    $12.00 : PASS
   "$90.00"    -->    $90.00 : PASS
   "$14.50"    -->    $14.50 : PASS
   "MKT"       -->       MKT : PASS

3) Verifying the Functionality of your Mathematical Operations:
   $10.50    + $1,400.99 = $1,411.49 : PASS
   $1,400.99 - $1,400.99 =     $0.00 : PASS
   $-51.52   +     $0.49 =   $-51.03 : PASS
   $0.49     *         4 =     $1.96 : PASS
   $-0.89    -    $12.00 =   $-12.89 : PASS
   $12.00    +    $90.00 =   $102.00 : PASS
   PASS: Cannot add a LIMIT price to a MARKET Price: (MKT + $10.50)
   PASS: Cannot subtract a LIMIT price from a MARKET Price: (MKT - $10.50)
   PASS: Cannot multiply a MARKET price: (MKT * 10)

4) Verifying the Functionality of your Boolean Checks:
   Value      | Negative Check |  MKT Check
   -------------------------------------------
   $10.50     |     PASS       |     PASS    
   $1,400.99  |     PASS       |     PASS    
   $-51.52    |     PASS       |     PASS    
   $0.49      |     PASS       |     PASS    
   $-0.89     |     PASS       |     PASS    
   $12.00     |     PASS       |     PASS    
   $90.00     |     PASS       |     PASS    
   $14.50     |     PASS       |     PASS    
   MKT        |     PASS       |     PASS    

5) Verifying the Functionality of your Boolean Comparisons:
   (Comparison to $14.50)
    Value      | greaterOrEqual  | greaterThan  | lessOrEqual  | lessThan
    -------------------------------------------------------------------------
    $10.50     |     PASS        |     PASS     |     PASS     |     PASS 
    $1,400.99  |     PASS        |     PASS     |     PASS     |     PASS 
    $-51.52    |     PASS        |     PASS     |     PASS     |     PASS 
    $0.49      |     PASS        |     PASS     |     PASS     |     PASS 
    $-0.89     |     PASS        |     PASS     |     PASS     |     PASS 
    $12.00     |     PASS        |     PASS     |     PASS     |     PASS 
    $90.00     |     PASS        |     PASS     |     PASS     |     PASS 
    $14.50     |     PASS        |     PASS     |     PASS     |     PASS 
    MKT        |     PASS        |     PASS     |     PASS     |     PASS 

6) Verifying your Flyweight Implementation:
    Price $10.50    is same object as new    $10.50: PASS
    Price $1,400.99 is same object as new    $10.50: PASS
    Price MKT       is same object as new       MKT: PASS
    Price $1,400.99 is same object as new       MKT: PASS

Price Tests Complete


1) Create and print the content of a valid Order using Tradable reference:
Tradable's toString: USER1 order: BUY 250 GE at $21.59 (Original Vol: 250, CXL'd Vol: 0), ID: USER1GE$21.5988078560796096

2) Create and print the content of a TradableDTO:
TradableDTO's toString: Product: GE, Price: $21.59, OriginalVolume: 250, RemainingVolume: 250, CancelledVolume: 0, User: USER1, Side: BUY, IsQuote: false, Id: USER1GE$21.5988078560796096

3) Attempt to create an order using INVALID data (Zero volume) - should throw an exception:
Properly handled an invalid volume -- error message is:
	Invalid Order Volume: 0

4) Change the cancelled and remaining volume of the order and display resulting tradable:
Tradable's toString: USER1 order: BUY 10 GE at $21.59 (Original Vol: 250, CXL'd Vol: 25), ID: USER1GE$21.5988078560796096

5) Change the Cancelled Volume of the order to greater than original volume - should throw an exception:
Properly handled an invalid cancelled volume -- error message is:
	Requested new Cancelled Volume (300) plus the Remaining Volume (10) exceeds the tradable's Original Volume (250)

6) Change the Remaining Volume of the order to greater than original volume - should throw an exception:
Properly handled an invalid remaining volume -- error message is: 
	Requested new Remaining Volume (300) plus the Cancelled Volume (10) exceeds the tradable's Original Volume (250)

7) Create and print the content of a valid Quote:
Quote's toString: USER2 quote: GE $21.56 x 100 (Original Vol: 100, CXL'd Vol: 0) [USER2GE88078580428652] - $21.62 x 100 (Original Vol: 100, CXL'd Vol: 0) [USER2GE88078580448285]

8) Display the individual Quote Sides of the new Quote object:
	QuoteSide's toString: $21.56 x 100 (Original Vol: 100, CXL'd Vol: 0) [USER2GE88078580428652]
	QuoteSide's toString: $21.62 x 100 (Original Vol: 100, CXL'd Vol: 0) [USER2GE88078580448285]

9) Attempt to create a quote using INVALID data (negative sell volume) - should throw an exception:
Properly handled an invalid volume -- error message is: Invalid SELL-Side Volume: -50

*/
