package net.sourceforge.pmd.eclipse.cpd;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.SimpleRenderer;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process CPD action menu. Run CPD against the selected project.
 * 
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/05/31 23:04:11  phherlin
 * Fix Bug 1190624: refactor CPD integration
 *
 * Revision 1.8  2004/04/19 22:25:12  phherlin
 * Upgrading to PMD v1.6
 *
 * Revision 1.7  2003/11/30 22:57:37  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.6.2.1  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.6  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 * Revision 1.5  2003/05/19 22:26:07  phherlin
 * Updating PMD engine to v1.05
 * Fixing CPD usage to conform to new engine implementation
 *
 */
public class CPDCheckProjectAction implements IObjectActionDelegate {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.CPDCheckProjectAction");
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) { // NOPMD:UnusedFormalParameter
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(final IAction action) { // NOPMD:UnusedFormalParameter
        final ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();
        if (sel instanceof IStructuredSelection) {
            final StructuredSelection ss = (StructuredSelection) sel;
            final Iterator i = ss.iterator();
            while (i.hasNext()) {
                final Object obj = i.next();
                if (obj instanceof IAdaptable) {
                    final IAdaptable adaptable = (IAdaptable) obj;
                    final IProject project = (IProject) adaptable.getAdapter(IProject.class);
                    if (project == null) {
                        log.warn("The selected object cannot adapt to a project");
                        log.debug("   -> selected object : " + obj);
                    } else {
                        this.detectCutAndPaste(project);
                    }
                } else {
                    log.warn("The selected object is not adaptable");
                    log.debug("   -> selected object : " + obj);
                }
            }
        }        
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(final IAction action, final ISelection selection) { // NOPMD:UnusedFormalParameter
    }
    
    /**
     * Run the DetectCutAndPaste command against the selected project
     * @param project a project
     */
    private void detectCutAndPaste(final IProject project) {
        try {
            final DetectCutAndPasteCmd cmd = new DetectCutAndPasteCmd();
            cmd.setProject(project);
            cmd.setRenderer(new SimpleRenderer());
            cmd.setReportName(PMDPluginConstants.SIMPLE_CPDREPORT_NAME);
            cmd.execute();
        } catch (CommandException e) {
            PMDPlugin.getDefault().showError("Error while detecting Cut & Paste", e); // TODO NLS
        }
    }

}