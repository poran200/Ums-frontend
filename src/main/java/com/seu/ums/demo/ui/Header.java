package com.seu.ums.demo.ui;


import com.seu.ums.demo.model.LoginToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class Header extends HorizontalLayout implements Serializable {
    private LoginToken loginToken;
     private Image logo;

    public String name;

    public void setName(String name) {
        this.name = name;
    }

    private   Label fulnameLable;
    public Header(HttpSession httpSession) {


        loginToken = (LoginToken) httpSession.getAttribute("user");
        if (loginToken == null)
            loginToken = new LoginToken();

         fulnameLable = new Label();
         logo = new Image();
         logo.setSrc("https://xu-university.com/wp-content/uploads/2019/06/XU-Exponential-University-of-applied-sciences-Logo-black-2.png");
         logo.setHeight("55px");
         Button logoutButton = new Button("Logout", VaadinIcon.EXIT.create());

         fulnameLable.setText("ID:"+loginToken.getUserId());
        fulnameLable.getStyle().set("margin-left","720px");

        logoutButton.addClickListener(event -> {
                    httpSession.removeAttribute("user");
                    logoutButton.getUI().ifPresent(ui -> ui.navigate(""));

                });

       add(logo,fulnameLable);
//
        logoutButton.getStyle().set("margin-left","150px");

//
       add(logoutButton);



    }

    public LoginToken getLoginToken() {
        return loginToken;
    }
}