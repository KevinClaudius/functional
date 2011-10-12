package functional;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import data.Department;
import data.Person;

public class Main {
  // Sample data
  private static final Iterable<Department> sampleDepartments = Lists.newArrayList(
        new Department(0, 40, "Engineering"),
        new Department(1, 35, "Sales"),
        new Department(2, 10, "Marketing"));
  private static final Iterable<Person> samplePeople = Lists.newArrayList(
        new Person(0, 29, "Joe Engineer", 0),
        new Person(1, 29, "Bob Engineer", 0),
        new Person(2, 52, "Mohammed", 1),
        new Person(3, 10, "McLovin", 1),
        new Person(4, 2, "Intern Jane", 2));
  
  // Predicates
  private static final Predicate<Department> isEngineering = new Predicate<Department>() {
        @Override public boolean apply(Department d) {
          return "Engineering".equals(d.name);
        }
      };
  private static final int engineeringDepartmentId = Iterables.find(sampleDepartments, isEngineering).id;
  private static final Predicate<Person> isPersonInEngineering = new Predicate<Person>() {
      @Override public boolean apply(Person p) {
        // engineeringDepartmentId could be replaced with the call in its initializer above.  
        //   This is called "referential transparency" in the functional programming world.  This is 
        //   an example of how you could cache that call to "find".
        return p.departmentId == engineeringDepartmentId;
      }
    };
  private static final Predicate<Person> isOld = new Predicate<Person>() {
        @Override
        public boolean apply(Person p) {
          return p.yearsAtCompany >= 15;
        }   
      };

  /**
   * @param args
   */
  public static void main(String[] args) {
    runSelectExample();
  }

  private static void runSelectExample() {
    // Imperative way
    int departmentId = 0;
    for (Department d : sampleDepartments) {
      if ("Engineering".equals(d.name)) {
        departmentId = d.id;
      }
    }
    int numberOfOldTimersInEng = 0;
    for (Person emp: samplePeople) {
      if (emp.departmentId == departmentId && emp.yearsAtCompany >= 15) {
        ++numberOfOldTimersInEng;
      }
    }
    System.out.println("numberOfOldTimersInEng (imperative): " + numberOfOldTimersInEng);
    
    // Functional way
    Iterable<Person> oldTimersInEng = filter(samplePeople, and(isOld, isPersonInEngineering)); // separation of concerns: a filter can be composed with predicates
    System.out.println("numberOfOldTimersInEng (functional): " + newArrayList(oldTimersInEng).size());
  }
}
