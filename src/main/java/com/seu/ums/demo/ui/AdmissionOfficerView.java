package com.seu.ums.demo.ui;

import com.seu.ums.demo.model.LoginToken;
import com.seu.ums.demo.model.Program;
import com.seu.ums.demo.model.Role;
import com.seu.ums.demo.model.Student;
import com.seu.ums.demo.service.LoginTokenService;
import com.seu.ums.demo.service.ProgramService;
import com.seu.ums.demo.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Route("admission")
public class AdmissionOfficerView extends AppLayout {
    private Map<Tab, Component> tab2Workspace;
    private Tabs tabs;
    private Grid<Student> studentGrid;
    private Dialog dialog;
    private StudentService studentService;
    private Binder<Student> studentBinder;
    private HttpSession httpSession;
    private ProgramService programService;
    private LoginTokenService loginTokenService;

    public AdmissionOfficerView(StudentService studentService, HttpSession httpSession, ProgramService programService, LoginTokenService loginTokenService) {
        this.studentService = studentService;
        this.httpSession = httpSession;
        this.programService = programService;
        this.loginTokenService = loginTokenService;
        initialize();
        Header header = new Header(httpSession);


        // header  check the user
        header.addAttachListener(event -> {
            LoginToken loginToken = header.getLoginToken();

            try {
                if (!loginToken.getRole().equals(Role.ADMISSION_OFFICER)) {
                    httpSession.removeAttribute("user");
                    header.getUI().ifPresent(ui -> ui.navigate(""));
                }
            }catch (NullPointerException e){
                httpSession.removeAttribute("user");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });
       addToNavbar(new DrawerToggle());
       tabs = new Tabs(dashborad(),user());
       tabs.setSelectedIndex(0);
       setContent(tab2Workspace.get(tabs.getSelectedTab()));
       tabs.setOrientation(Tabs.Orientation.VERTICAL);
       tabs.addSelectedChangeListener(selectedChangeEvent -> {
         Tab selectedTab =selectedChangeEvent.getSelectedTab();
         Component component =tab2Workspace.get(selectedTab);
         setContent(component);
     });




     addToDrawer(tabs);
    setContent(tab2Workspace.get(tabs.getSelectedTab()));
      addToNavbar(header);
     studentGrid.setItems(studentService.findall());

    }
    private void initialize(){
        studentBinder = new Binder<>();
        dialog = new Dialog();
        tab2Workspace = new HashMap<>();
        studentGrid = new Grid<>();
    }
    private Tab dashborad(){
         Span label = new Span("Admission");
         Tab tab = new Tab(new HorizontalLayout(label));
         tab2Workspace.put(tab, deshboradView());
         return tab;
    }

    private VerticalLayout deshboradView() {
        VerticalLayout deshlayout = new VerticalLayout();
        H2 h1 = new H2("Student List");
        deshlayout.add(h1);
        Button addStudent= new Button("Add student", VaadinIcon.PLUS_CIRCLE.create());
        addStudent.getStyle().set("float","right");
        setStudentGrid();
        addStudent.addClickListener(buttonClickEvent -> dialog.open());
        deshlayout.add(addStudent,studentGrid);
        createStudent();
        return deshlayout;
    }

    private Tab user(){
        Span lable = new Span("Profile");
        Tab tab = new Tab(new HorizontalLayout(lable));
        tab2Workspace.put(tab, getProfileView());
        return tab;
    }

    private  VerticalLayout getProfileView() {
        VerticalLayout profilePage = new VerticalLayout();
         H2 h2 = new H2("Profile");
         profilePage.add(h2);
         return profilePage;
    }

//    private Tab logout(){
//        Span lable = new Span("LogOut");
//        Button logoutButton =  new Button("LogOut",VaadinIcon.SIGN_OUT.create());
//        logoutButton.addClickListener(buttonClickEvent -> logoutButton.getUI().ifPresent(ui -> ui.navigate("")));
//        Tab tab = new Tab(logoutButton);
//        tab2Workspace.put(tab, new VerticalLayout());
//        return tab;
//    }
    private   Button updateButton= new Button("Update",VaadinIcon.INSERT.create());
    TextField idField = new TextField("Student ID","6 digit id");
    private  void createStudent() {
          dialog.setCloseOnOutsideClick(false);
          dialog.setCloseOnEsc(false);
        FormLayout formLayout = new FormLayout();

          Button confirmButton = new Button("Save",VaadinIcon.CHECK.create());
          Button cancelButton = new Button("Cancel",VaadinIcon.CLOSE.create(),event->{
              dialog.close();
          });

        TextField nameField = new TextField("Student Name","Full name");
        EmailField emailField = new EmailField("Student Email","email");
        TextField batchField= new TextField("Batch"," current batch ");
        DatePicker dobPicker = new DatePicker("Date of Birth");
        Select<String> programSelect = new Select<>();
        programSelect.setItems(programService.getProgramList().stream().map(Program::getProgram_title).collect(Collectors.toList()));
        programSelect.setLabel("Program");
        studentBinder
                .forField(idField)
                .asRequired("Enter id")
                .withValidator(id-> id.length()==6,"Id must be 6 digit")
                .withConverter(new StringToLongConverter(""))
                .bind(Student::getId,Student::setId);
        studentBinder
                .forField(nameField)
                .asRequired("required field")
                .bind(Student::getName,Student::setName);
        studentBinder
                .forField(emailField)
                .asRequired()
                .bind(Student::getEmail,Student::setEmail);
        studentBinder
                .forField(batchField)
                .asRequired()
                .withConverter(new StringToIntegerConverter("Batch must be integer"))
                .bind(Student::getBatch,Student::setBatch);
        studentBinder
                .forField(dobPicker)
                .withValidator(dob -> DAYS.between(dob, LocalDate.now()) > 16 * 365, "Students should be at least 16 years old!")
                .bind(Student::getDob,Student::setDob);
        studentBinder
                .forField(programSelect)
                .asRequired()
                .bind(Student::getProgram,Student::setProgram);

        confirmButton.addClickListener(event->{
           //TODO fix save student button
            Student student = new Student();
            LoginToken loginToken = new LoginToken();
            try {
                studentBinder.writeBean(student);
                Student saveStudent= studentService.create(student);
                loginToken .setUserId(idField.getValue());
                loginToken.setUserName(nameField.getValue());
                loginToken.setLoginPassword(idField.getValue());
                loginToken.setRole(Role.STUDENT);
                loginTokenService.createToken(loginToken);
                Notification.show(student.getName()+" Saved !");
                Notification.show("Temporary password is Student id");
                studentGrid.setItems(studentService.findall());
                dialog.close();

            }catch (HttpClientErrorException.NotAcceptable e){
                  Notification.show(student.getId()+" Already Exist !");
            } catch (ValidationException e) {
                System.err.println(e);
                Notification.show(e.getMessage());
            } catch (Exception e){
                Notification.show(e.getMessage());
            }

        });



         formLayout.add(idField,nameField,emailField,dobPicker,programSelect,batchField);
         dialog.add(formLayout,confirmButton,updateButton,cancelButton);


    }

    private void setStudentGrid() {

        studentGrid
                .addColumn(Student::getId)
                .setWidth("250px")
                .setFlexGrow(1)
                .setHeader("Student ID");
        studentGrid
                .addColumn(Student::getName)
                .setWidth("250px")
                .setFlexGrow(1)
                .setHeader("Name");
        studentGrid
                .addColumn(Student::getBatch)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Batch");
         studentGrid
                 .addColumn(Student::getEmail)
                 .setFlexGrow(1)
                 .setHeader("Email");

        studentGrid
                .addColumn(Student::getDob)
                .setFlexGrow(1)
                .setHeader("Date of birth");
        studentGrid
                .addColumn(Student::getProgram)
                .setFlexGrow(1)
                .setHeader("Program");
         studentGrid
                 .addComponentColumn(this::getEditButton)
                 .setWidth("50px")
                 .setFlexGrow(0);



    }

    private  Component getEditButton(Student student) {
           Button button =  new Button("",VaadinIcon.EDIT.create(),buttonClickEvent -> {
               studentBinder.readBean(student);
               dialog.open();
               dialog.setCloseOnOutsideClick(false);
               dialog.setCloseOnEsc(false);
               idField.setReadOnly(true);
               updateButton.addClickListener(buttonClickEvent1 -> {
                   try {
                       studentService.updateStudent(student);
                       Notification.show(student.getId()+ "Updated !").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                       dialog.close();
                   } catch (Exception e) {
                       Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                   }
               });
           });
           return button;

    }

}
