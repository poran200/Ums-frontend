package com.seu.ums.demo.ui;

import com.seu.ums.demo.eception.ResourseNotFoundException;
import com.seu.ums.demo.model.*;
import com.seu.ums.demo.service.CourseService;
import com.seu.ums.demo.service.EmployeeService;
import com.seu.ums.demo.service.SectionService;
import com.seu.ums.demo.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Route("coordinator")
public class CoordinatorView extends AppLayout {
    private Map<Tab , Component> containerView;
    private Tabs tabs;
    private Grid<Course> courseGrid;
    private Grid<Scetion> sectionGrid;
    private Grid<Scetion> sectionGridWorkSpace;
    private Dialog createSectionDialog;
    private Binder<Scetion> sectionBinder ;
    private CourseService courseService;
    private SectionService sectionService;
    private StudentService studentService;
    private HttpSession httpSession;
    private EmployeeService employeeService;
    private VerticalLayout verticalLayout;
    private  Dialog sectionGridDialog;





    public CoordinatorView(CourseService courseService, SectionService sectionService, StudentService studentService, HttpSession httpSession, EmployeeService employeeService) {
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.studentService = studentService;
        this.httpSession = httpSession;
        this.employeeService = employeeService;
        initialize();
        Header header = new Header(httpSession);
        header.addAttachListener(event -> {
            LoginToken loginToken = header.getLoginToken();

            try {
                if (!loginToken.getRole().equals(Role.COORDINATOR)) {
                    httpSession.removeAttribute("user");
                    header.getUI().ifPresent(ui -> ui.navigate(""));
                }
            }catch (NullPointerException e){
                httpSession.removeAttribute("user");
                header.getUI().ifPresent(ui -> ui.navigate(""));
            }
        });
        addToNavbar(new DrawerToggle());
        tabs= new Tabs(dashBoard(),workspace(),user());
        tabs.setSelectedIndex(0);
        setContent(containerView.get(tabs.getSelectedTab()));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab  selectedTab = selectedChangeEvent.getSelectedTab();
            Component component = containerView.get(selectedTab);
            setContent(component);
        });
        addToDrawer(tabs);
        addToNavbar(header);

        LoginToken loginToken = header.getLoginToken();
        Employee employee = employeeService.getEmployeeById(loginToken.getUserId());
        String programName = employee.getProgram();
        courseGrid.setItems(courseService.findByProgram(programName));
        createSectionDialog();
        sectionGridWorkSpace.setItems(sectionService.findByFaculty(loginToken.getUserId()));
        setSectionGridWorkSpace();


    }

    private Tab workspace() {
        Span lable =new Span("Workspace");
        Tab tab = new Tab(new HorizontalLayout(lable));
        containerView.put(tab, workspaceView());

        return tab;
    }

    private  VerticalLayout workspaceView() {
        VerticalLayout workspaceLayout = new VerticalLayout();
        H3 h3 =new H3("Section List for the Current semester:");
        workspaceLayout.add(h3);
         workspaceLayout.add( sectionGridWorkSpace);
         return workspaceLayout;

    }



    private Button addSectionButton = new Button("Add Section", VaadinIcon.PLUS_CIRCLE.create(), event-> createSectionDialog.open());
    private Button closeButton= new Button("Cancel",VaadinIcon.CLOSE.create(),event->{
            sectionGrid.removeAllColumns();
            sectionGridDialog.close();});
    private  Course course =new Course();
    private H4 fromLabel = new H4();
    private TextField sectionCourseCodeField= new TextField("Course Code");
    private TextField sectionCourseTitleField= new TextField("Course Title");



    public void initialize(){
        containerView = new HashMap<>();
        tabs = new Tabs();
        courseGrid = new Grid<>();
        sectionGrid = new Grid<>();
        verticalLayout = new VerticalLayout();
        sectionGridDialog = new Dialog();
        createSectionDialog = new Dialog();
        sectionBinder = new Binder<>();
        sectionGridWorkSpace = new Grid<>();



    }
    public Tab dashBoard(){
        Span label = new Span("Dashboard");
        Tab tab = new Tab(new HorizontalLayout(label));
        containerView.put(tab,dashBoardView());
        return tab;
    }

    private VerticalLayout  dashBoardView() {
        VerticalLayout  dashboardLayout = new VerticalLayout();
        H2 h2 = new H2("Courses Offered by ");
         dashboardLayout.add(h2);
        setCourseGrid();
        createSectionDialog();
//        setSectionGrid();
         dashboardLayout.add(courseGrid);
        return  dashboardLayout;
    }
    private Tab user(){
        Span span = new Span("Profile");
        Tab tab = new Tab(new HorizontalLayout(span));
        containerView.put(tab, userView());
        return tab;
    }

     private  VerticalLayout userView() {
        VerticalLayout  userLayout = new VerticalLayout();
        H3 h3 = new H3("Profile");
        userLayout.add(h3);
        return userLayout;

    }

    public void setCourseGrid() {

        courseGrid
                .addColumn(Course::getCode)
                .setFlexGrow(1)
                .setHeader("Course Code");
        courseGrid
                .addColumn(Course::getTitle)
                .setFlexGrow(1)
                .setHeader("Course title");
        courseGrid
                .addColumn(Course::getCredit)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Credit");
        courseGrid
                .addColumn(Course::getProgram)
                .setFlexGrow(1)
                .setHeader("Program");
        courseGrid .addItemClickListener(courseItemClickEvent -> {
                  sectionGridDialog.open();

                 course = courseItemClickEvent.getItem();
                 fromLabel.setText("Create Section for-> "+course.getTitle()+"("+course.getCode()+")");
                 sectionCourseCodeField.setValue(course.getCode());
                 sectionCourseTitleField.setValue(course.getTitle());
                 setSectionGridDialog();
                 sectionGrid.setItems(sectionService.findByCourseCode(course.getCode()));


        });

    }



    public void setSectionGridDialog() {
        sectionGridDialog.setCloseOnEsc(false);
         sectionGridDialog.setCloseOnOutsideClick(false);

        sectionGrid
                .addColumn(Scetion::getSectionID)
                .setHeader("Section Id");
        sectionGrid
                .addColumn(scetion -> scetion.getCourse().getCode())
                .setHeader("Course Code");
        sectionGrid
                .addColumn( scetion -> scetion.getCourse().getTitle())
                .setHeader("Course Title");
        sectionGrid
                .addColumn(Scetion::getSection_number)
                .setHeader("Section number");
        sectionGrid
                .addColumn(Scetion::getFaculty)
                .setHeader("Faculty");
        sectionGrid
                .addColumn(Scetion::getCapacity)
                .setHeader("Limit");
        sectionGrid
                .addColumn(scetion -> scetion.getStudentInfos().size())
                .setHeader("Advised");
        sectionGrid
                .addColumn(Scetion::getSemester_id)
                .setHeader("Semester id");
           verticalLayout.add(sectionGrid,addSectionButton,closeButton);
           sectionGridDialog.add( verticalLayout);
           sectionGridDialog.setWidth("1200px");

    }

    public void setSectionGridWorkSpace() {
        sectionGridWorkSpace
                .addColumn(Scetion::getSectionID)
                .setHeader("Section Id");
        sectionGridWorkSpace
                .addColumn( scetion -> scetion.getCourse().getCode() )
                .setHeader("Course Code");
        sectionGridWorkSpace
                .addColumn(scetion -> scetion.getCourse().getTitle())
                .setFlexGrow(2)
                .setHeader("Course Title");
        sectionGridWorkSpace
                .addColumn(Scetion::getSection_number)
                .setHeader("Section number");
        sectionGridWorkSpace
                .addColumn(Scetion::getFaculty)
                .setHeader("Faculty");
        sectionGridWorkSpace
                .addColumn(Scetion::getCapacity)
                .setHeader("Limit");
        sectionGridWorkSpace
                .addColumn(scetion -> scetion.getStudentInfos().size())
                .setHeader("Advised");
        sectionGridWorkSpace
                .addColumn(Scetion::getSemester_id)
                .setHeader("Semester id");

    }

    //stupid ....
     private FormLayout formLayout = new FormLayout();
    private Select<String> facultySelectField= new Select<>();
    private TextField sectionCapacityField = new TextField("Section Capacity");
    private TextField semesterNumberField = new TextField("Semester Number");
    private TextField sectionNumberField= new TextField("Section Number");
    private Button saveSectionButton = new Button("Save",VaadinIcon.CHECK.create());
    private Button cnacleFromButton = new Button("Cancel",VaadinIcon.CLOSE.create(), event-> {
        createSectionDialog.close();
        clear();
    } );
   //stupid variable
   private   int test;
   public void clear(){
        sectionNumberField.clear();
        sectionCapacityField.clear();
         semesterNumberField.clear();
         facultySelectField.clear();
   }
    public void createSectionDialog() {
        createSectionDialog.setCloseOnOutsideClick(false);
        createSectionDialog.setCloseOnEsc(false);
        sectionCourseCodeField.setReadOnly(true);
        sectionCourseTitleField.setReadOnly(true);

              facultySelectField.setItems( employeeService.getEmployees().stream()
                    .filter(employee -> employee.getRole().equals(Role.Faculty) || employee.getRole().equals(Role.COORDINATOR))
                    .map(Employee::getInitial)
                    .collect(Collectors.toList()
                  ));

        facultySelectField.setLabel("Faculty");
        formLayout.add(sectionCourseCodeField,sectionCourseTitleField,sectionNumberField,facultySelectField,sectionCapacityField,semesterNumberField);

         sectionBinder
                 .forField(sectionNumberField)
                 .asRequired()
                 .withConverter(new StringToIntegerConverter("section number must be integer"))
                 .bind(Scetion::getSection_number,Scetion::setSection_number);
         sectionBinder
                 .forField(facultySelectField)
                 .asRequired()
                 .bind(Scetion::getFaculty,Scetion::setFaculty);
         sectionBinder
                 .forField(sectionCapacityField)
                 .asRequired()
                 .withConverter(new StringToIntegerConverter("must be  integer"))
                 .bind(Scetion::getCapacity,Scetion::setCapacity);
         sectionBinder
                 .forField(semesterNumberField)
                 .asRequired()
                 .withConverter(new StringToIntegerConverter("must be integer"))
                 .bind(Scetion::getSemester_id,Scetion::setSemester_id);

         saveSectionButton.addClickListener(buttonClickEvent -> {
               Scetion scetion = new Scetion();

               try {
                   sectionBinder.writeBean(scetion);
                    Scetion savedSection= sectionService.createSection(sectionCourseCodeField.getValue(),scetion);
                   System.out.println(sectionCourseCodeField.getValue());
                    //stupid idea
                    test= 1;
                   sectionGrid.setItems(sectionService.findByCourseCode( sectionCourseCodeField.getValue() ));

                   Notification.show("Section Created successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                   createSectionDialog.close();
               } catch (ValidationException  e) {
                   Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);

               } catch (HttpClientErrorException | ResourseNotFoundException e){
                   //stupid idea fix
                   if (test==1){
                       System.err.println("ok");
                      // Notification.show("ok");
                   }else {
                       System. err.println(test+"from test");

                       Notification.show("Section Already exist !").addThemeVariants(NotificationVariant.LUMO_ERROR);

                   }
               }
               catch ( Exception e){
                   System.out.println(e.getMessage()+" from Exception");


               }
         });
         createSectionDialog.add(fromLabel,formLayout,saveSectionButton,cnacleFromButton);


    }


}
