import fr.ufc.m2info.svam.Article;
import fr.ufc.m2info.svam.ArticleDB;
import fr.ufc.m2info.svam.Caisse;
import fr.ufc.m2info.svam.Scanette;
import junit.framework.Assert;

import java.util.*;

/**
 * Adapter used to bridge the gap between the model level and the implementation level.
 */
public class ScanAdapter {

    //property 1 : Before unlocking the scanner, it's not possible to scan or remove any item.
    int property_1_1 = 0; // never scanOK before debloquer
    int property_1_2 = 0; // never supprimerOK before debloquer
    int property_1_3 = 0; // never panier > 0 before debloquer

    //property 3 : The use of the scanner ends either by a transmission to the cash register or an abandon.
    int property_3_1 = 0; // never scanOK && supprimerOk && scanRefInconnue && supprimerInexistant after transmission0 || transmission1
    int property_3_2 = 0; // never scanOK && supprimerOk && scanRefInconnue && supprimerInexistant after abandon until debloquer

    //property 5 : If an item has not been recognized during the shoping, the cashier has to open session imediatly after
    //the transmission to the cash register.
    int property_5_1 = 0; // after scanRefInconnue until abandon ouvrirSession directly follows transmission 0




    Map<String, Integer> couvertureProp1 = initProp1();

    private Map<String, Integer> initProp1(){
        Map<String, Integer> couv = new HashMap<>();
        couv.put("0-scanOK-1", 0);
        couv.put("1-supprimerOK-1", 0);
        couv.put("state0", 0);
        couv.put("state1", 0);
        couv.put("state2", 0);
        return couv;
    }

    public void printCoverage(){
        System.out.println(couvertureProp1);
    }


    // array of all articles
    final public Article[] allArticles = {
            new Article(3560070048786l, 0.87, "Cookies choco"),
            new Article(3017800238592l, 2.20, "Daucy Curry vert de légumes riz graines de courge et tournesol"),
            new Article(3560070976478l, 1.94, "Poulet satay et son riz"),
            new Article(3046920010856l, 2.01, "Lindt Excellence Citron à la pointe de Gingembre"),
            new Article(8715700110622l, 0.96, "Ketchup"),
            new Article(3570590109324l, 7.48, "Vin blanc Arbois Vieilles Vignes"),
            new Article(3520115810259l, 8.49, "<Mont> d'or moyen Napiot"),
            new Article(3270190022534l, 0.58, "Pâte feuilletée"),
            new Article(8718309259938l, 4.65, "Soda stream saveur agrumes"),
            new Article(3560071097424l, 2.40, "Tartelettes carrées fraise"),
            new Article(3017620402678l, 1.86, "Nutella 220g"),
            new Article(3245412567216l, 1.47, "Pain de mie"),
            new Article(45496420598l, 54.99, "Jeu switch Minecraft"),
            new Article(3560070139675l, 1.94, "Boîte de 110 mouchoirs en papier"),
            new Article(3020120029030l, 1.70, "Cahier Oxford 90 pages petits carreaux"),
            new Article(7640164630021l, 229.90, "Robot éducatif Thymio"),
            new Article(5410188006711l, 2.15, "Tropicana Tonic Breakfast")
    };

    // scanner
    ArticleDB dbScanette = newArticleDBScanette();
    Scanette scan = new Scanette(dbScanette);

    // cash register
    ArticleDB dbCaisse = newArticleDBCaisse();
    Caisse caisse = new Caisse(dbCaisse);

    List<Long> basket = new ArrayList<Long>();


    /**
     * Resets the scanner and the cash register with new instances.
     */
    public void reset() {
        scan = new Scanette(dbScanette);
        caisse = new Caisse(dbCaisse);
        basket.clear();
        property_1_1 = 0;
        property_1_2 = 0;
        property_1_3 = 0;
        property_3_1 = 0;
        property_3_2 = 0;
        property_5_1 = 0;
    }

    public int debloquer() {
        erreurProperty5_1();
        if (property_1_1 == 0) {
            property_1_1 = 1;
        }
        if (property_1_2 == 0) {
            property_1_2 = 1;
        }
        if (property_1_3 == 0) {
            property_1_3 = 1;
        }

        if (property_1_1 == 2) {
            System.out.println("Erreur property_1_1 : debloquer");
        }
        if (property_1_2 == 2) {
            System.out.println("Erreur property_1_2 : debloquer");
        }
        if (property_1_3 == 2) {
            System.out.println("Erreur property_1_3 : debloquer");
        }
        if(property_3_2 == 1){
            property_3_2 = 2;
        }
        basket.clear();
        return scan.debloquer();
    }

    public void abandon() {
        property_5_1 = 0;
        if(property_3_2 == 0 || property_3_2 == 2){
            property_3_2 = 1;
        }
        scan.abandon();
        basket.clear();
    }

    public int scanAchatOK() {
        erreurProperty5_1();
        if (property_1_1 == 0 || property_1_1 == 2) {
            property_1_1 = 2;
        }
        if(property_3_1 == 0){
            property_3_1 = 2;
        }
        if(property_3_2 == 0){
            property_3_2 = 2;
        }
        if(property_3_1 == 1){
            property_3_1 = -1;
            System.out.println("Erreur property_3_1 : scan ok");
        }
        if(property_3_2 == 1){
            property_3_2 = -1;
            System.out.println("Erreur property_3_2 : scan ok");
        }
        int i = (int) (Math.random() * (allArticles.length - 2));
        int j = (int) (Math.random() * basket.size());
        basket.add(j, allArticles[i].getCodeEAN13());
        return scan.scanner(allArticles[i].getCodeEAN13());
    }

    public int scanRefInconnue() {
        erreurProperty5_1();
        if(property_5_1 == 0){
            property_5_1 = 1;
        }
        if(property_3_1 == 0){
            property_3_1 = 2;
        }
        if(property_3_2 == 0){
            property_3_2 = 2;
        }
        if(property_3_1 == 1){
            property_3_1 = -1;
            System.out.println("Erreur property_3_1 : scan ref inconnue");
        }
        if(property_3_2 == 1){
            property_3_2 = -1;
            System.out.println("Erreur property_3_2 : scan ref inconnue");
        }
        int i = (int) (Math.random() * 2);
        return scan.scanner(allArticles[allArticles.length - 1 - i].getCodeEAN13());
    }

    public int supprimerOK() {
        erreurProperty5_1();
        if (property_1_2 == 0) {
            property_1_2 = 2;
        }
        if(property_3_1 == 0){
            property_3_1 = 2;
        }
        if(property_3_2 == 0){
            property_3_2 = 2;
        }
        if(property_3_1 == 1){
            property_3_1 = -1;
            System.out.println("Erreur property_3_1 : supprimer ok");
        }
        if(property_3_2 == 1){
            property_3_2 = -1;
            System.out.println("Erreur property_3_2 : supprimer ok");
        }
        Set<Article> set = scan.getArticles();
        Assert.assertTrue(set.size() > 0);
        int i = (int) (Math.random() * set.size());
        Iterator<Article> it = set.iterator();
        Article a = it.next();
        while (it.hasNext() && i > 1) {
            i--;
            a = it.next();
        }
        basket.remove(basket.indexOf(a.getCodeEAN13()));
        return scan.supprimer(a.getCodeEAN13());
    }

    public int supprimerKO() {
        erreurProperty5_1();
        if(property_3_1 == 0){
            property_3_1 = 2;
        }
        if(property_3_2 == 0){
            property_3_2 = 2;
        }
        if(property_3_1 == 1){
            property_3_1 = -1;
            System.out.println("Erreur property_3_1 : supprimer inexistant");
        }
        if(property_3_2 == 1){
            property_3_2 = -1;
            System.out.println("Erreur property_3_2 : supprimer inexistant");
        }
        return scan.supprimer(0l);
    }


    public int transmission() {
        erreurProperty5_1();
        int valueTransmission = scan.transmission(caisse);
        if(property_5_1 == 1){
            if (valueTransmission == 0) {
                property_5_1 = 2;
            }
        }
        if(property_3_1 == 2){
            if(valueTransmission != 1){
                property_3_1 = 3;
            }
        }else if(property_3_1 == 0){
            property_3_1 = 1;
        }

        return valueTransmission;
    }

    public int relectureOK() {
        erreurProperty5_1();
        int i = 0, r = 0;
        while (i < basket.size() && i < 12) {
            r = scan.scanner(basket.get(i));
            i++;
        }
        return r;
    }

    public int relectureKO() {
        erreurProperty5_1();
        return scan.scanner(0l);
    }

    public int ouvrirSession() {
        if(property_5_1 == 2){
            property_5_1 = 3;
        }
        return caisse.ouvrirSession();
    }

    public int fermerSession() {
        erreurProperty5_1();
        return caisse.fermerSession();
    }

    public int ajouterAchat() {
        erreurProperty5_1();
        if(property_1_3 == 0){
            property_1_3 = 2;
        }
        int i = (int) (Math.random() * allArticles.length);
        basket.add(0, allArticles[i].getCodeEAN13());
        return caisse.scanner(allArticles[i].getCodeEAN13());
    }

    public int supprimerAchat() {
        erreurProperty5_1();
        long b = basket.remove(0);
        return caisse.supprimer(b);
    }

    public double payer() {
        erreurProperty5_1();
        System.out.println("c'est la faute a payer");
        double d = 0.0;
        for (long code : basket) {
            for (Article a : allArticles) {
                if (a.getCodeEAN13() == code) {
                    d += a.getPrixUnitaire();
                    break;
                }
            }
        }
        return caisse.payer(Math.ceil(d));
    }


    /**
     * Initializes a database for the scanners with all articles except the 2 with a barcode ending with "1".
     */
    private ArticleDB newArticleDBScanette() {
        erreurProperty5_1();
        Set<Article> r = new HashSet<Article>();
        for (Article a : allArticles) {
            if (a.getCodeEAN13() % 10 != 1) {
                r.add(a);
            }
        }
        ArticleDB db = new ArticleDB();
        db.init(r);
        return db;
    }

    /**
     * Initializes a database for the cash registers with all articles (no exception).
     */
    private ArticleDB newArticleDBCaisse() {
        erreurProperty5_1();
        Set<Article> r = new HashSet<Article>();
        for (Article a : allArticles) {
            r.add(a);
        }
        ArticleDB db = new ArticleDB();
        db.init(r);
        return db;
    }

    private void erreurProperty5_1(){
        if(property_5_1 == 2){
            System.out.println("Erreur property_5_1");
        }
    }

}
