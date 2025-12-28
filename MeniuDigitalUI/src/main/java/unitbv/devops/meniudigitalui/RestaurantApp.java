package unitbv.devops.meniudigitalui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import unitbv.devops.meniudigitalui.db.DatabaseInitializer;
import unitbv.devops.meniudigitalui.db.DbSanityCheck;
import unitbv.devops.meniudigitalui.db.HibernateUtil;
import unitbv.devops.meniudigitalui.ui.LoginUI;

public class RestaurantApp extends Application {
    private SessionFactory sessionFactory;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            HibernateUtil.initialize();
            this.sessionFactory = HibernateUtil.getSessionFactory();

            DatabaseInitializer.initializeDatabase(sessionFactory);
            DbSanityCheck.printCounts(sessionFactory);
        } catch (Exception e) {
            System.err.println("[FATAL] Failed during init: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        LoginUI loginUI = new LoginUI(sessionFactory);
        loginUI.show(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
