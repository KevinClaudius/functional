package data;

public class Person {
  public int departmentId;
  public int id;
  public String name;
  public int yearsAtCompany;
  
  public Person(int id, int yearsAtCompany, String name, int departmentId) {
    this.id = id;
    this.departmentId = departmentId;
    this.yearsAtCompany = yearsAtCompany;
    this.name = name;
  }
}
