package team.globaloptima;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "orders")
@NamedQueries({
        @NamedQuery(
                name = "Order.findOrders",
                query = "SELECT o FROM Order o"
        )
})
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "foodId")
    private Integer foodId;
    @Column(name = "foodName")
    private String foodName;
    @Column(name = "customerId")
    private Integer customerId;
    @Column(name = "customerAddress")
    private String customerAddress = "Missing";
    @Column(name = "supplierId")
    private Integer supplierId;
    @Column(name = "supplierAddress")
    private String supplierAddress = "Missing";
    @Column(name = "suppliedStatus")
    private String suppliedStatus = "Not cooking";
    @Column(name = "deliveredStatus")
    private String deliveredStatus = "Not delivering";
    @Column(name = "estimatedCookingMinutes", columnDefinition = "integer default 0")
    private Integer estimatedCookingMinutes = 0;
    @Column(name = "estimatedDeliveryMinutes", columnDefinition = "integer default 0")
    private Integer estimatedDeliveryMinutes = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFoodId() {
        return this.foodId;
    }
    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return this.foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public String getSuppliedStatus() {
        return suppliedStatus;
    }

    public void setSuppliedStatus(String suppliedStatus) {
        this.suppliedStatus = suppliedStatus;
    }

    public String getDeliveredStatus() {
        return deliveredStatus;
    }

    public void setDeliveredStatus(String deliveredStatus) {
        this.deliveredStatus = deliveredStatus;
    }

    public Integer getEstimatedCookingMinutes() {
        return estimatedCookingMinutes;
    }

    public void setEstimatedCookingMinutes(Integer estimatedCookingMinutes) {
        this.estimatedCookingMinutes = estimatedCookingMinutes;
    }

    public Integer getEstimatedDeliveryMinutes() {
        return estimatedDeliveryMinutes;
    }

    public void setEstimatedDeliveryMinutes(Integer estimatedDeliveryMinutes) {
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
    }

}
