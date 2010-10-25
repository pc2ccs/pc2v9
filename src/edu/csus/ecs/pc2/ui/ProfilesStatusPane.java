package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.ProfileChangeStatusList;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.ProfileChangeStatus;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesStatusPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3089291613784484371L;

    private JPanel buttonPane = null;

    private MCLB profilesListBox = null;
    
    private ProfileChangeStatusList profileStatusList = new ProfileChangeStatusList(); // @jve:decl-index=0:

    /**
     * This method initializes
     * 
     */
    public ProfilesStatusPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(457, 199));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getProfilesListBox(), BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Profile Status Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new GridBagLayout());
            buttonPane.setPreferredSize(new Dimension(35, 35));
        }
        return buttonPane;
    }

    /**
     * This method initializes profilesListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getProfilesListBox() {
        if (profilesListBox == null) {
            profilesListBox = new MCLB();
            
            String [] cols = {"Site", "Name", "Status","Description", "When"};
            
            profilesListBox.addColumn(cols);
            
            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());
            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountColumnComparator());

            int idx = 0;

            profilesListBox.setColumnSorter(idx++, numericStringSorter, 1); // Site
            profilesListBox.setColumnSorter(idx++, sorter, 2); // Name
            profilesListBox.setColumnSorter(idx++, sorter, 3); // Status
            profilesListBox.setColumnSorter(idx++, sorter, 4); // Description
            profilesListBox.setColumnSorter(idx++, sorter, 5); // When
            
        }
        return profilesListBox;
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        getContest().addSiteListener(new SiteListenerImplementation());
        getContest().addLoginListener(new LoginListenerImplementation());
        getContest().addProfileListener(new ProfileListenerImplementation());

    }
    
    protected void reloadListBox() {
        
        for (Site site: getContest().getSites()){
            updateRow (site);
        }
    }
    
    private Object[] buildProfileRow(Site site) {
        int numberColumns = profilesListBox.getColumnCount();
        Object[] c = new String[numberColumns];
        
        return c;
    }

    
    private void updateRow (Site site) {
        int row = profilesListBox.getIndexByKey(site.getElementId());
        if (row == -1) {
            Object[] objects = buildProfileRow(site);
            profilesListBox.addRow(objects, site.getElementId());
        } else {
            Object[] objects = buildProfileRow(site);
            profilesListBox.replaceRow(objects, row);
        }
        profilesListBox.autoSizeAllColumns();
    }
    
    protected void updateRow(ProfileChangeStatus status) {
        Site site = getContest().getSite(status.getSiteNumber());
        updateRow(site);
    }


    
    /**
     * Login Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void loginRemoved(final LoginEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void loginDenied(LoginEvent event) {
            // updateLoginList(event.getClientId(), event.getConnectionHandlerID());
        }
        
        public void loginRefreshAll(LoginEvent event) {
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
            
        }
    }

    /**
     * Profile Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ProfileListenerImplementation implements IProfileListener {

        public void profileAdded(ProfileEvent event) {
        }

        public void profileChanged(ProfileEvent event) {
            reloadListBox();
        }

        public void profileRemoved(ProfileEvent event) {
        }

        public void profileRefreshAll(ProfileEvent profileEvent) {
            reloadListBox();
        }
    }

    /**
     * Listen for site profile changes.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class SiteListenerImplementation implements ISiteListener{

        public void siteProfileStatusChanged(SiteEvent event) {
            ProfileChangeStatus profileChangeStatus = (ProfileChangeStatus) profileStatusList.get(event.getSite());
            profileChangeStatus.setStatus(event.getProfileStatus());
            profileChangeStatus.setProfile(event.getProfile());
            profileStatusList.update(profileChangeStatus);
            final ProfileChangeStatus status = profileChangeStatus;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateRow(status);
                }
            });
        }
        
        public void siteAdded(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteRemoved(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
            
        }

        public void siteChanged(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteLoggedOn(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void siteLoggedOff(SiteEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }

        public void sitesRefreshAll(SiteEvent siteEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadListBox();
                }
            });
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
