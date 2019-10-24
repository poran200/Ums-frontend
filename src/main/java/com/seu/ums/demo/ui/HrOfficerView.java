package com.seu.ums.demo.ui;

import com.seu.ums.demo.model.Employee;
import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.model.Program;
import com.seu.ums.demo.model.Role;
import com.seu.ums.demo.service.EmployeeService;
import com.seu.ums.demo.service.LoginTokenService;
import com.seu.ums.demo.service.ProgramService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("hrm")
public class HrOfficerView extends AppLayout {
    private Map<Tab, Component> containerView;
    private Tabs tabs;
    private Grid<Employee> employeeGrid;
    private Dialog dialog;
    private EmployeeService employeeService;
    private Binder<Employee> employeeBinder;
    private HttpSession httpSession;
    private ProgramService programService;
    private LoginTokenService loginTokenService;

    public HrOfficerView(EmployeeService employeeService, HttpSession httpSession, ProgramService programService, LoginTokenService loginTokenService) {
        this.employeeService = employeeService;
        this.httpSession = httpSession;
        this.programService = programService;
        this.loginTokenService = loginTokenService;
        initialize();
        Header header = new Header(httpSession);

        header.addAttachListener(event -> {
            LoginToken loginToken = header.getLoginToken();

            try {
                if (!loginToken.getRole().equals(Role.DEPUTY_REGISTRAR_HRM)) {
                    httpSession.removeAttribute("user");
                    header.getUI().ifPresent(ui -> ui.navigate(""));
                }
            }catch (NullPointerException e){
                httpSession.removeAttribute("user");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });


        addToNavbar(new DrawerToggle());
        tabs = new Tabs(recruitment(),user());
        tabs.setSelectedIndex(0);
        setContent(containerView.get(tabs.getSelectedTab()));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab selectedTab = selectedChangeEvent.getSelectedTab();
            Component component = containerView.get(selectedTab);
            setContent(component);
        });
        addToDrawer(tabs);
        addToNavbar(header);
        employeeGrid.setItems(employeeService.getEmployees());

    }

    private Tab user() {
        Span span = new Span("Profile");
        Tab tab = new Tab(new HorizontalLayout(span));
        containerView.put(tab,getProfileView());
        return tab;
    }

    private  VerticalLayout getProfileView() {
        VerticalLayout progfileView= new VerticalLayout();
        H2 h2 = new H2("Profile");
        progfileView.add(h2);
        return progfileView;
    }

    private  Tab recruitment() {
        Span span = new Span("Recruitment");
        Tab tab = new Tab(new HorizontalLayout(span));
        containerView.put(tab,recruitmentView());
        return tab;
    }

    private VerticalLayout recruitmentView() {
        VerticalLayout verticalLayout= new VerticalLayout();
        H2 h2 = new H2("Employee List");
        verticalLayout.add(h2);
        Button addEmployee = new Button("Add Employee", VaadinIcon.PLUS.create(),buttonClickEvent -> dialog.open());
        verticalLayout.add(addEmployee,employeeGrid);
        setEmployeeGrid();
        createEmployee();
        return verticalLayout;
    }

    private  void createEmployee() {
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        FormLayout formLayout = new FormLayout();
        TextField initialField= new TextField("Initial");
        TextField nameField = new TextField("Full Name");
        EmailField emailField= new EmailField("Email");
        PasswordField passwordField =new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");

        Select<String> programSelect= new Select<>();
        List<String> list = (programService.getProgramList().stream().map(Program::getProgram_title).collect(Collectors.toList()));
        list.add("Academic");
        list.add("Human Resource");
        list.add("Admission");
        list.add("Examination");
        programSelect.setItems(list);
        programSelect.setLabel("Department");


        Select<Role> roleSelect = new Select<>();
        roleSelect.setItems(Role.ADMISSION_OFFICER,Role.COORDINATOR,Role.DEPUTY_REGISTRAR_Academic,Role.DEPUTY_REGISTRAR_HRM,Role.EXAM_OFFICER,Role.Faculty);
        roleSelect.setLabel("Role");
        employeeBinder
                .forField(initialField)
                .asRequired()

                .bind(Employee::getInitial,Employee::setInitial);
        employeeBinder
                .forField(nameField)
                .asRequired()
                .bind(Employee::getName,Employee::setName);
        employeeBinder
                 .forField(emailField)
                 .asRequired()
                 .bind(Employee::getEmail,Employee::setEmail);
        employeeBinder
                 .forField(programSelect)
                 .asRequired()
                 .bind(Employee::getProgram,Employee::setProgram);
        employeeBinder
                .forField(roleSelect)
                .asRequired()
                .bind(Employee::getRole,Employee::setRole);
        employeeBinder
                .forField(passwordField)
                .asRequired()
               // .withValidator(paass-> paass.length() <=0,"")
                .bind(Employee::getLoginpassword,Employee::setLoginpassword);
        employeeBinder
                .forField(confirmPasswordField)
                .asRequired()
                .withValidator(password->!password.equals(passwordField.getValue()),"password not match");


        Button saveButton = new Button("Save",VaadinIcon.CHECK.create());
        Button cancelButton = new Button("Cancel",VaadinIcon.CLOSE.create(),buttonClickEvent -> dialog.close());
         saveButton.addClickListener(buttonClickEvent -> {
            Employee employee = new Employee();
             LoginToken loginToken = new LoginToken();
             if (! passwordField.getValue().equals(confirmPasswordField.getValue())){

                 Notification notification = new Notification();
                  notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                  notification.setText("Password not match !");
                  notification.setDuration(3000);
                  notification.open();

                 confirmPasswordField.focus();
                 confirmPasswordField.setRequiredIndicatorVisible(true);
             }else {
                 try {
                     employeeBinder.writeBean(employee);
                     loginToken.setUserId(initialField.getValue());
                     loginToken.setUserName(nameField.getValue());
                     loginToken.setLoginPassword(passwordField.getValue());
                     loginToken.setRole(roleSelect.getValue());
                     loginTokenService.createToken(loginToken);
                     Employee savedEmployee = employeeService.create(employee);
                     Notification.show(employee.getName()+"Saved ").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                     employeeGrid.setItems(employeeService.getEmployees());
                     dialog.close();

                 } catch (ValidationException e) {
                     Notification.show(e.getMessage());
                 } catch (Exception e) {
                     Notification.show(e.getMessage());
                 }
             }
         });

        formLayout.add(initialField,nameField,emailField,programSelect,passwordField,roleSelect,confirmPasswordField);
        dialog.add(formLayout,saveButton,cancelButton);




    }



    private void  setEmployeeGrid() {
        employeeGrid
                .addColumn(Employee::getName)
                .setFlexGrow(1)
                .setHeader("Name");
        employeeGrid
                .addColumn(Employee::getEmail)
                .setFlexGrow(1)
                .setHeader("Email");
        employeeGrid
                .addColumn(Employee::getProgram)
                .setFlexGrow(1)
                .setHeader("Department");
        employeeGrid
                .addColumn(Employee::getRole)
                .setFlexGrow(1)
                .setHeader("Role");

    }

    private void initialize() {
        containerView= new HashMap<>();
        tabs= new Tabs();
        dialog= new Dialog();
        employeeGrid= new Grid<>();
        employeeBinder = new Binder<>();
    }
}
