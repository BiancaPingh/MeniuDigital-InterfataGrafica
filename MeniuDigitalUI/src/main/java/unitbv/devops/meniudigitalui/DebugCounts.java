package unitbv.devops.meniudigitalui;

import unitbv.devops.meniudigitalui.db.HibernateUtil;
import unitbv.devops.meniudigitalui.repository.MasaRepository;
import unitbv.devops.meniudigitalui.repository.ProdusRepository;

public class DebugCounts {
    public static void main(String[] args) {
        HibernateUtil.initialize();
        try {
            var produse = new ProdusRepository().findAll();
            var mese = new MasaRepository().findAll();

            System.out.println("produse=" + (produse == null ? "null" : produse.size()));
            System.out.println("mese=" + (mese == null ? "null" : mese.size()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (HibernateUtil.getSessionFactory() != null) {
                HibernateUtil.getSessionFactory().close();
            }
        }
    }
}
