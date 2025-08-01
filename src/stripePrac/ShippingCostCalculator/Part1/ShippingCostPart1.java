package stripePrac.ShippingCostCalculator.Part1;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShippingCostPart1 {

    private static final String ORDER_FILE_PATH = "/Users/chiragchawla/Practice/InterviewPrac/src/stripePrac/ShippingCostCalculator/Part1/Order.json";
    private static final String SHIPPING_COST_FILE_PATH = "/Users/chiragchawla/Practice/InterviewPrac/src/stripePrac/ShippingCostCalculator/Part1/ShippingCost.json";

    public static void main(String[] args) {
        int cost = calculateShippingCost(ORDER_FILE_PATH,SHIPPING_COST_FILE_PATH);
        System.out.println(cost);
    }

    private static int calculateShippingCost(String orderFilePath, String shippingCostFilePath){
        Order order = parseOrderFile(orderFilePath);
        System.out.println(order);
        Map<String, List<ProductItem>> shippingCostByCountry = parseShippingCost(shippingCostFilePath);
        if(!isValidInput(order,shippingCostByCountry)) return -1;
        List<ProductItem> productItemList = shippingCostByCountry.get(order.country);
        Map<String, Integer> productCostMap = productItemList.stream().collect(Collectors.toMap(ProductItem::getProduct,ProductItem::getCost));
        return calculateCost(order,productCostMap);

    }

    private static boolean isValidInput(Order order, Map<String, List<ProductItem>> shippingCostByCountry) {
        return order!=null &&
                shippingCostByCountry != null &&
                order.getCountry()!=null &&
                shippingCostByCountry.containsKey(order.country);
    }

    private static int calculateCost(Order order, Map<String, Integer> productCostMap) {
        int totalCost = 0;
        try{
            for(OrderItem orderItem : order.getOrderItems()){
                //calculate cost for each item
                if (!productCostMap.containsKey(orderItem.getProduct())) {
                    System.out.println("No cost found for product: " + orderItem.getProduct());
                } else
                    totalCost += productCostMap.getOrDefault(orderItem.getProduct(),0)* orderItem.getQuantity();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return totalCost;
    }

    private static Map<String, List<ProductItem>> parseShippingCost(String shippingCostFilePath) {
        String shippingData = readFromFile(shippingCostFilePath);
        Type shippingType = new TypeToken<Map<String, List<ProductItem>>>(){}.getType();
        return new Gson().fromJson(shippingData,shippingType);
    }

    private static Order parseOrderFile(String orderFilePath) {
        Gson gson = new Gson();
        Order order=null;
        String orderData = readFromFile(orderFilePath);
        try {
            order = gson.fromJson(orderData,Order.class);
        } catch (JsonSyntaxException e) {
            System.out.println("unable to parse Order file : " + e.getMessage());
        }
        return order;
    }

    private static String readFromFile(String path) {
        String data = null;
        try{
            data = Files.readString(Paths.get(path));
        } catch(Exception e){
            System.err.println("Error reading from file " + path + ", : " + e.getMessage());

        }
        return data;
    }

    private static class Order{

        @SerializedName("country")
        String country;

        @SerializedName("items")
        List<OrderItem> orderItems;


        public Order(String country, List<OrderItem> orderItems) {
            this.country = country;
            this.orderItems = orderItems;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "country='" + country + '\'' +
                    ", orderItems=" + orderItems +
                    '}';
        }
    }

    private static class OrderItem{

        @SerializedName("product")
        String product;

        @SerializedName("quantity")
        int quantity;

        public OrderItem(String product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "OrderItem{" +
                    "product='" + product + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    private static class ProductItem{
        @SerializedName("product")
        String product;

        @SerializedName("cost")
        int cost;

        public ProductItem(String product, int cost) {
            this.product = product;
            this.cost = cost;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "ProductItem{" +
                    "product='" + product + '\'' +
                    ", cost=" + cost +
                    '}';
        }
    }
}
