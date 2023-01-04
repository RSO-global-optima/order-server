package team.globaloptima;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "suppliers")
@NamedQueries({
        @NamedQuery(
                name = "Supplier.findSuppliers",
                query = "SELECT s FROM Supplier s"
        )
})
public class Supplier implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "supplierName")
    private String supplierName;
    @Column(name = "address")
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
