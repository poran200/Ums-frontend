package com.seu.ums.demo.ui;

import com.seu.ums.demo.model.Course;
import com.seu.ums.demo.model.Program;
import com.seu.ums.demo.service.CourseService;
import com.seu.ums.demo.service.ProgramService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("register")
public class DeputyRegisterView extends AppLayout {
    private Map<Tab,Component> tabContainerView;
    private Tabs tabs;
    private Grid<Program> programGrid;
    private Grid<Course> courseGrid;
    private Binder<Program> programBinder;
    private Binder<Course> courseBinder;
    private HttpSession httpSession;
    private CourseService courseService;
    private ProgramService programService;
    private Dialog createProgramDialog;
    private  Dialog courseViewDialog;
    private  Dialog createCourseDailog;
    private VerticalLayout verticalLayout;



    public DeputyRegisterView(HttpSession httpSession,CourseService courseService, ProgramService programService) {

        this.httpSession = httpSession;
        this.courseService = courseService;
        //this.sectionService = sectionService;
        this.programService = programService;


        initialize();
        Header header = new Header(httpSession);
        addToNavbar(new DrawerToggle());
        tabs = new Tabs(dashboard());

        tabs.setSelectedIndex(0);
        setContent(tabContainerView.get(tabs.getSelectedTab()));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab selectedTab = selectedChangeEvent.getSelectedTab();
             Component component = tabContainerView.get(selectedTab);
             setContent(component);
        });
        addToDrawer(tabs);
        setContent(tabContainerView.get(tabs.getSelectedTab()));
        addToNavbar(header);

    }
    Button createCoursebutton= new Button("Add Course",VaadinIcon.PLUS.create(), event->{
        createCourseDailog.open();
    });
    Button cancelButton = new Button("Cancel",VaadinIcon.CLOSE.create(), event-> {
        courseGrid.removeAllColumns();
        courseViewDialog.close();
    });

    private Tab dashboard() {
        Span label = new Span("Dashboard");
        Tab tab = new Tab(new HorizontalLayout(label));
        tabContainerView.put(tab, dashboardView());
        return tab;
    }

    private VerticalLayout dashboardView() {
        VerticalLayout deshLayout = new VerticalLayout();
        H2 h1 = new H2("Program List");
        H2  h2 = new H2("Course List");
        deshLayout.add(h1);
        Button  addProgram = new Button("Add Program", VaadinIcon.PLUS_CIRCLE.create());

        addProgram.getStyle().set("float","right");

        setProgramGrid();
       // setCourseGrid();
        addProgram.addClickListener(event->createProgramDialog.open());
       // addCourse.addClickListener(event->courseDialog.open());
        deshLayout.add(addProgram,programGrid);
        createProgram();
        createCourse();

        return deshLayout;

    }

    private void createCourse() {
        //TODO
        createCourseDailog.setCloseOnOutsideClick(false);
        createCourseDailog.setCloseOnEsc(false);
        FormLayout formLayout = new FormLayout();

        Select<String> programname= new Select<>();
        programname.setItems(programService.getProgramList().stream()
                .map(Program::getProgram_title).collect(Collectors.toList()));
        programname.setLabel("Program");

        TextField courseCodeField= new TextField("Course code","i.e CSE101");
        TextField courseTitleField= new TextField("Course title","Computer fundamental");

         Select<Integer> criditeSelectfield= new Select<>();
        List<Integer> creditHour= new ArrayList<>();
                     creditHour.add(1);
                     creditHour.add(3);
            criditeSelectfield.setItems(creditHour);
         criditeSelectfield.setRequiredIndicatorVisible(true);
         criditeSelectfield.setPlaceholder("credit hour");

        Button saveCoursebutton = new Button("Save",VaadinIcon.CHECK_CIRCLE.create());
        Button  fromclosebutton = new Button("Cancel",VaadinIcon.CLOSE.create(),event-> createCourseDailog.close());

        formLayout.add( courseCodeField,programname,courseTitleField,criditeSelectfield);
        createCourseDailog.add(formLayout,saveCoursebutton,fromclosebutton);
        courseBinder
                .forField(courseCodeField)
                .asRequired()

                .withValidator(code-> code.length() <=6,"course code must be 6 charter")
                .bind(Course::getCode,Course::setCode);
         courseBinder
                 .forField(courseTitleField)
                 .asRequired()
                 .bind(Course::getTitle,Course::setTitle);
         courseBinder
                  .forField(programname)
                  .asRequired()
                 // .withValidator(program-> program.isEmpty(),"select the program")
                  .bind(Course::getProgram,Course::setProgram);
         courseBinder
                 .forField(criditeSelectfield)
                 .asRequired()
                 .bind(Course::getCredit,Course::setCredit);
          saveCoursebutton.addClickListener(event->{
              Course course =new Course();
              try {
                  courseBinder.writeBean(course);
                  Course saveCourse= courseService.createCourse(programname.getValue(),course);
                  Notification.show("Saved "+course.getTitle()).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                  courseGrid.setItems(courseService.findByProgram(programname.getValue()));
                  createCourseDailog.close();
              } catch (ValidationException e) {
                  e.printStackTrace();
                  Notification.show(e.getMessage());
              }catch (HttpClientErrorException e){
                  Notification.show(course.getCode()+" already exist").addThemeVariants(NotificationVariant.LUMO_ERROR);
              }
              catch (Exception e){
                  Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
              }
          });

    }

    private void createProgram() {
        createProgramDialog.setCloseOnOutsideClick(false);
        createProgramDialog.setCloseOnEsc(false);
        FormLayout  formLayout = new FormLayout();
        TextField  programNameField= new TextField("Program Name","i.e BSc in CSE");
        TextField  requireCreditfield=new TextField("Require Credit","minimum require credit ");
        TextField  rqCgpafield = new TextField("Require CGPA","minimum cgpa");
        Button savebutton = new Button("Save",VaadinIcon.CHECK_CIRCLE.create());
        Button cancelButton = new Button("Cancel",VaadinIcon.CLOSE.create()
                ,buttonClickEvent -> createProgramDialog.close());
        formLayout.add(programNameField,requireCreditfield,rqCgpafield);
        programBinder
                .forField(programNameField)
                .asRequired()
                .withValidator(name-> name.length() >=6,"name should be more then 6 characters")
                .bind(Program::getProgram_title,Program::setProgram_title);
        programBinder
                .forField(requireCreditfield)
                .asRequired()
                .withConverter(new StringToIntegerConverter("Credit must be integer"))
                .bind(Program::getMinimum_credits,Program::setMinimum_credits);
        programBinder
                .forField(rqCgpafield)
                .asRequired()
                .withConverter(new StringToDoubleConverter("Enter float value"))
                .bind(Program::getMinimum_CGPA,Program::setMinimum_CGPA);
        savebutton.addClickListener(event->{
            Program newProgram = new Program();
            try {
                programBinder.writeBean(newProgram);
                Program saveProgram = programService.create(newProgram);
                programGrid.setItems(programService.getProgramList());

                Notification.show(saveProgram.getProgram_title()+" Created")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                createProgramDialog.close();
            }catch (ValidationException e){
                Notification.show(e.getMessage());
                System.err.println(e);

            }catch (HttpClientErrorException e){
                Notification.show(newProgram.getProgram_title()+" This name Already exist")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

                programNameField.focus();
                System.err.println(e.getMessage());
            }
            catch (Exception e){
              Notification.show(e.getMessage());
            }
        });
        createProgramDialog.add(formLayout,savebutton,cancelButton);


    }

    private void setCourseGrid() {
        //TODO
         courseViewDialog.setCloseOnOutsideClick(false);
         courseViewDialog.setCloseOnEsc(false);

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

//           courseDialog.add(courseGrid);
//          courseDialog.add(cancelButton);

        verticalLayout.add(courseGrid,createCoursebutton,cancelButton);
         courseViewDialog.add(verticalLayout);
         courseViewDialog.setWidth("1000px");

        courseGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);



    }

    private void setProgramGrid() {
        // TODO

        programGrid
                .addColumn(Program::getProgram_title)
                .setFlexGrow(1)
                .setHeader("Program Name");
        programGrid
                .addColumn(Program::getMinimum_credits)
                .setFlexGrow(1)
                .setHeader("Require minimum credits");
        programGrid
                .addColumn(Program::getMinimum_CGPA)
                .setFlexGrow(1)
                .setHeader("Require minimum CGPA");
        programGrid.setItems(programService.getProgramList());
        programGrid.asSingleSelect();
        programGrid.addItemClickListener(programItemClickEvent ->{

            courseViewDialog.open();
             setCourseGrid();
             courseGrid.setItems(courseService.findByProgram(programItemClickEvent.getItem().getProgram_title()));

        });


    }

    private void initialize() {
        programGrid = new Grid<>();
        courseGrid = new Grid<>();
        programBinder= new Binder<>();
        courseBinder = new Binder<>();
        tabContainerView= new HashMap<>();
        createProgramDialog = new Dialog();
        courseViewDialog = new Dialog();
        createCourseDailog = new Dialog();
        verticalLayout = new VerticalLayout();

    }
}
