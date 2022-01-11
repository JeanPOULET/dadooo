import junit.framework.Assert;
import nz.ac.waikato.modeljunit.*;

/**
 *  FSM of the scanner + cash register usage.
 *  (implementing the provided usage model)
 */
public class ScanetteFSM implements FsmModel
{
    /** Variable representing the current state */
    private int state;

    /** Variable representing the adapter to which actions are transmitted to be executed on the SUT */
    private ScanAdapter adapter;

    private int panier = 0;

    /**
     *  Constructor. Initializes the data.
     */
    public ScanetteFSM() {
        state = 0;
        adapter = new ScanAdapter();
    }

    /**
     *  Inherited from the FsmModel interface.
     *  Provides a Comparable object that characterizes the current state.
     */
    @Override
    public Integer getState() {
        return state;
    }

    /**
     *  Inherited from the FsmModel interface.
     *  Provides a Comparable object that characterizes the current state.
     */
    @Override
    public void reset(boolean testing) {
        state = 0;
        adapter.reset();
        panier = 0;
    }

    /* All the transitions of the automaton are public methods:
     *  - labelled with @Action
     *  - returning nothing (void)
     *  - accompanied by a boolean method of the same name suffixed by "Guard" to express the guard of the transition
     */

    /**
     * State labels:
     * 0 == blocked
     * (4) 100 == shopping 1-00 (empty basket, no unknown references)
     * (6) 110 == shopping 1-10 (non-empty basket, no unknown references)
     * (5) 101 == shopping 1-01 (empty basket, unknown references)
     * (7) 111 == shopping 1-11 (non-empty basket, unknown references)
     */

    @Action
    public void debloquer() {
        // realizes the transition on the System Under Test
        int r = adapter.debloquer();
        // check expected result
        Assert.assertEquals(0, r);
        // evolution of the FSM state
        state = 4;
        panier = 0;
    }
    public boolean debloquerGuard() {
        return state == 0;
    }
    
    @Action
    public void abandon() {
        state = 0;
        adapter.abandon();
    }
    public boolean abandonGuard() {
        return state > 0 && state < 8; 
    }

    // Actions that add/remove items  
    @Action
    public void scanOK() {
        int r = adapter.scanAchatOK();
        Assert.assertEquals(0, r);
        if (state == 4 || state == 5) {
            state += 2;
        }
        panier++;
    }
    public boolean scanOKGuard() {
        return state >= 4 && state <= 7;
    }
    
    @Action
    public void scanRefInconnue() {
        int r = adapter.scanRefInconnue();
        Assert.assertEquals(-2, r);
        if (state == 4 || state == 6) {
            state++;
        }
    }
    public boolean scanRefInconnueGuard() {
        return state >= 4 && state <= 7;
    }

    @Action
    public void supprimerOK() {
        int r = adapter.supprimerOK();
        Assert.assertEquals(0, r);
        panier--;
        if (panier == 0) {
            state -= 2;
        }
    }
    public boolean supprimerOKGuard() {
        return (state == 6 || state == 7);
    }

    @Action
    public void supprimerInexistant() {
        int r = adapter.supprimerKO();
        Assert.assertEquals(-2, r);
    }
    public boolean supprimerInexistantGuard() {
        return (state >= 4 && state <= 7);
    }


    @Action
    public void transmission() {
        int r = adapter.transmission();
        Assert.assertTrue(r == 1 || r == 0);
        switch (state) {
            case 4:
            case 5:
                Assert.assertEquals(0, r);
                state = 16;
                break;
            case 6:
                state = (r == 0) ? 20 : 10;
                break;
            case 7:
                state = (r == 0) ? 19 : 11;
                break;
            case 12:
                Assert.assertEquals(0, r);
                state = 20;
                break;
            case 13:
                Assert.assertEquals(0, r);
                state = 19;
        }
    }
    public boolean transmissionGuard() {
        return (state >= 4 && state <= 7) || (state >= 12 && state <= 13); 
    }


    @Action
    public void relectureOK() {
        int r = adapter.relectureOK();
        Assert.assertEquals(0, r);
        state += 2;
    }
    public boolean relectureOKGuard() {
        return state >= 10 && state <= 11;
    }
    
    @Action
    public void relectureKO() {
        int r = adapter.relectureKO();
        Assert.assertEquals(-3, r);
        state = 15;  // trap state
    }
    public boolean relectureKOGuard() {
        return state >= 10 && state <= 11;
    }

    @Action
    public void ouvrirSession() {
        int r = adapter.ouvrirSession();
        Assert.assertEquals(0, r);
        if (state == 16) {
            state = 22;
        }
        else {
            state = 21;
        }
    }
    public boolean ouvrirSessionGuard() {
        return state == 16 || state == 19 || state == 20;
    }

    @Action
    public void fermerSession() {
        int r = adapter.fermerSession();
        Assert.assertEquals(0, r);
        state = (state == 21) ? 20 : 31;
    }
    public boolean fermerSessionGuard() {
        return state == 21 || state == 22;
    }

    @Action
    public void ajouterAchat() {
        int r = adapter.ajouterAchat();
        Assert.assertEquals(0, r);
        panier++;
        state = 21;
    }
    public boolean ajouterAchatGuard() {
        return state == 21 || state == 22;
    }
    
    @Action
    public void supprimerAchat() {
        int r = adapter.supprimerAchat();
        Assert.assertEquals(0, r);
        panier--;
        if (panier == 0) {
            state = 22;
        }
    }
    public boolean supprimerAchatGuard() {
        return state == 21;
    }

    @Action
    public void payer() {
        double r = adapter.payer();
        Assert.assertEquals(0, r, 1.0);
        state = 30;
    }
    public boolean payerGuard() {
        return state == 20;
    }
}
