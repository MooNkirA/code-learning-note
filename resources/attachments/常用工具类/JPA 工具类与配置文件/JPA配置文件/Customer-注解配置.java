package day61.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ʵ����
 * 
 * @author MoonZero
 */
// ���������Ĺ�ϵ
@Entity
@Table(name = "cst_customer")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	// �����������ֶεĹ�ϵ
	// ����ID�����������ɲ���
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_id")
	private Long custId; // bigint(32) NOT NULL AUTO_INCREMENT COMMENT '�ͻ����(����)',
	// ע�⣬������������ֶ�ͬ�������Բ�����@Column
	@Column(name = "cust_name")
	private String custName; // varchar(32) NOT NULL COMMENT '�ͻ�����(��˾����)',
	@Column(name = "cust_source")
	private String custSource; // varchar(32) DEFAULT NULL COMMENT '�ͻ���Ϣ��Դ',
	@Column(name = "cust_industry")
	private String custIndustry; // varchar(32) DEFAULT NULL COMMENT '�ͻ�������ҵ',
	@Column(name = "cust_level")
	private String custLevel; // varchar(32) DEFAULT NULL COMMENT '�ͻ�����',

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

	// ���������������ע��������� get���������棨��������������棩
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