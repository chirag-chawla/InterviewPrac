package stripePrac.ShippingCostCalculator.Part1;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShippingCostPart2 {

    private static final String ORDER_FILE_PATH = "/Users/chiragchawla/Practice/InterviewPrac/src/stripePrac/ShippingCostCalculator/Part1/Order.json";
    private static final String SHIPPING_COST_FILE_PATH = "/Users/chiragchawla/Practice/InterviewPrac/src/stripePrac/ShippingCostCalculator/Part1/ShippingCost2.json";

    public static void main(String[] args) {
        int cost = calculateShippingCost(ORDER_FILE_PATH, SHIPPING_COST_FILE_PATH);
        System.out.println(cost);
    }

    private static class Order{

        @SerializedName("country")
        String country;

        @SerializedName("items")
        List<ItemOrder> itemOrders;

        public Order(String country, List<ItemOrder> itemOrders) {
            this.country = country;
            this.itemOrders = itemOrders;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public List<ItemOrder> getItemOrders() {
            return itemOrders;
        }

        public void setItemOrders(List<ItemOrder> itemOrders) {
            this.itemOrders = itemOrders;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "country='" + country + '\'' +
                    ", itemOrders=" + itemOrders +
                    '}';
        }
    }

    private static class ItemOrder{

        @SerializedName("product")
        String product;

        @SerializedName("quantity")
        int quantity;

        public ItemOrder(String product, int quantity) {
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
            return "ItemOrder{" +
                    "product='" + product + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    private static class Cost{
        @SerializedName("minQuantity")
        Integer minQuantity;

        @SerializedName("maxQuantity")
        Integer maxQuantity;

        @SerializedName("cost")
        int cost;

        public Cost(int minQuantity, int maxQuantity, int cost) {
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.cost = cost;
        }

        public Integer getMinQuantity() {
            return minQuantity;
        }

        public void setMinQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
        }

        public Integer getMaxQuantity() {
            return maxQuantity;
        }

        public void setMaxQuantity(Integer maxQuantity) {
            this.maxQuantity = maxQuantity;
        }

        public Integer getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "Cost{" +
                    "minQuantity=" + minQuantity +
                    ", maxQuantity=" + maxQuantity +
                    ", cost=" + cost +
                    '}';
        }
    }

    private static class ProductLine{
        @SerializedName("product")
        String product;

        @SerializedName("costs")
        List<Cost> costs;

        public ProductLine(String product, List<Cost> costs) {
            this.product = product;
            this.costs = costs;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public List<Cost> getCost() {
            return costs;
        }

        public void setCost(List<Cost> cost) {
            this.costs = cost;
        }

        @Override
        public String toString() {
            return "ProductLine{" +
                    "product='" + product + '\'' +
                    ", cost=" + costs +
                    '}';
        }
    }

    private static int calculateShippingCost(String orderFilePath, String shippingCostFilePath){
        Order order = parseOrderFile(orderFilePath);
        Map<String, List<ProductLine>> countryWiseShippingCost = parseShippingCostFile(shippingCostFilePath);
        if(!isInputValid(order,countryWiseShippingCost)){
            return -1;
        }
        List<ProductLine> productLineList = countryWiseShippingCost.get(order.getCountry());
        Map<String, List<Cost>> productCostMap = productLineList.stream().collect(Collectors.toMap(ProductLine::getProduct, ProductLine::getCost));
        return calculateCost(order,productCostMap);
    }

    private static int calculateCost(Order order, Map<String, List<Cost>> productCostMap){
        int totalCost = 0;
        for(ItemOrder itemOrder : order.getItemOrders()){
            List<Cost> costs = productCostMap.get(itemOrder.getProduct());
            if(costs==null){
                System.out.println("Order item not present in product list");
            } else {
                costs = costs.stream().sorted(Comparator.comparingInt(Cost::getMinQuantity)).collect(Collectors.toList());
                int remainingQuantity= itemOrder.getQuantity();
                for(Cost cost : costs){
                    int minQ;
                    if(cost.getMinQuantity()==null || cost.getMinQuantity()==0){
                        minQ = 1;
                    } else {
                        minQ = cost.getMinQuantity();
                    }
                    int maxQ = cost.getMaxQuantity() != null ? cost.getMaxQuantity() : Integer.MAX_VALUE;
                    int applicableQuantity = Math.min(remainingQuantity,maxQ)-minQ+1;
                    totalCost += applicableQuantity*cost.getCost();
                    if(remainingQuantity<=0) break;
                }
            }
        }
        return totalCost;
    }

    private static boolean isInputValid(Order order, Map<String, List<ProductLine>> countryWiseShippingCost) {
        return order!=null &&
                countryWiseShippingCost!=null &&
                countryWiseShippingCost.containsKey(order.getCountry());
    }

    private static Map<String, List<ProductLine>> parseShippingCostFile(String shippingCostFilePath) {
        String shippingData = readFromFile(shippingCostFilePath);
        Type shippingCostType = new TypeToken<Map<String, List<ProductLine>>>(){}.getType();
        return new Gson().fromJson(shippingData,shippingCostType);
    }

    private static Order parseOrderFile(String orderFilePath){
        String orderData = readFromFile(orderFilePath);
        return new Gson().fromJson(orderData, Order.class);
    }

    private static String readFromFile(String path){
        String data = null;
        try {
            data= Files.readString(Paths.get(path));
        } catch (Exception e) {
            System.out.println("Error reading file" + e.getMessage());
        }
        return data;
    }
}
