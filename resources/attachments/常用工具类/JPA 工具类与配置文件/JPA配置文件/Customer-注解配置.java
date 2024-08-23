package day61.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 实体类
 * 
 * @author MoonZero
 */
// 配置类与表的关系
@Entity
@Table(name = "cst_customer")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	// 配置属性与字段的关系
	// 配置ID与主键的生成策略
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_id")
	private Long custId; // bigint(32) NOT NULL AUTO_INCREMENT COMMENT '客户编号(主键)',
	// 注意，如果属性名和字段同名，可以不配置@Column
	@Column(name = "cust_name")
	private String custName; // varchar(32) NOT NULL COMMENT '客户名称(公司名称)',
	@Column(name = "cust_source")
	private String custSource; // varchar(32) DEFAULT NULL COMMENT '客户信息来源',
	@Column(name = "cust_industry")
	private String custIndustry; // varchar(32) DEFAULT NULL COMMENT '客户所属行业',
	@Column(name = "cust_level")
	private String custLevel; // varchar(32) DEFAULT NULL COMMENT '客户级别',

	public Customer() {
	}

	public Customer(Long custId, String custName, String custSource, String custIndustry, String custLevel) {
		super();
		this.custId = custId;
		this.custName = custName;
		this.custSource = custSource;
		this.custIndustry = custIndustry;
		this.custLevel = custLevel;
	}

	public Customer(String custName, String custSource) {
		this.custName = custName;
		this.custSource = custSource;
	}

	// 配置在属性上面的注解可以配置 get方法的上面（建议放在属性上面）
	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustSource() {
		return custSource;
	}

	public void setCustSource(String custSource) {
		this.custSource = custSource;
	}

	public String getCustIndustry() {
		return custIndustry;
	}

	public void setCustIndustry(String custIndustry) {
		this.custIndustry = custIndustry;
	}

	public String getCustLevel() {
		return custLevel;
	}

	public void setCustLevel(String custLevel) {
		this.custLevel = custLevel;
	}

	@Override
	public String toString() {
		return "Customer [custId=" + custId + ", custName=" + custName + ", custSource=" + custSource
				+ ", custIndustry=" + custIndustry + ", custLevel=" + custLevel + "]";
	}
}