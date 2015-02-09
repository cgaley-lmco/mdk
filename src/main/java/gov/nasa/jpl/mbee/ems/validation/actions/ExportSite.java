package gov.nasa.jpl.mbee.ems.validation.actions;

import gov.nasa.jpl.mbee.ems.ExportUtility;
import gov.nasa.jpl.mbee.ems.sync.OutputQueue;
import gov.nasa.jpl.mbee.ems.sync.Request;
import gov.nasa.jpl.mgss.mbee.docgen.validation.IRuleViolationAction;
import gov.nasa.jpl.mgss.mbee.docgen.validation.RuleViolationAction;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nomagic.magicdraw.annotation.Annotation;
import com.nomagic.magicdraw.annotation.AnnotationAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;

public class ExportSite extends RuleViolationAction implements AnnotationAction, IRuleViolationAction {

    private static final long serialVersionUID = 1L;
    private Package element;
    
    public ExportSite(Package e) {
        //JJS--MDEV-567 fix: changed 'Export' to 'Commit'
        //
        super("ExportSite", "Commit site", null, null);
        this.element = e;
    }
    
    @Override
    public boolean canExecute(Collection<Annotation> arg0) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Collection<Annotation> annos) {
        JSONObject send = new JSONObject();
        JSONArray infos = new JSONArray();
        Set<Element> set = new HashSet<Element>();
        for (Annotation anno: annos) {
            Element e = (Element)anno.getTarget();
            if (e instanceof Package) {
                set.add(e);
                JSONObject elementOb = ExportUtility.fillId(e, null);
                elementOb.put("specialization", ExportUtility.fillPackage((Package)e, null));
                infos.add(elementOb);
            }
        }
        if (!ExportUtility.okToExport(set))
            return;
        send.put("elements", infos);
        send.put("source", "magicdraw");
        String url = ExportUtility.getPostElementsUrl();
        if (url == null) {
            return;
        }
        OutputQueue.getInstance().offer(new Request(url, send.toJSONString()));
        /*if (ExportUtility.send(url, send.toJSONString()) != null) {
            this.removeViolationsAndUpdateWindow(annos);
        }*/
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!ExportUtility.okToExport(element))
            return;
        JSONArray elements = new JSONArray();
        JSONObject send = new JSONObject();
        JSONObject elementOb = ExportUtility.fillId(element, null);
        elementOb.put("specialization", ExportUtility.fillPackage(element, null));
        elements.add(elementOb);
        send.put("elements", elements);
        send.put("source", "magicdraw");
        String url = ExportUtility.getPostElementsUrl();
        if (url == null) {
            return;
        }
        OutputQueue.getInstance().offer(new Request(url, send.toJSONString()));
        /*if (ExportUtility.send(url, send.toJSONString()) != null) {
            this.removeViolationsAndUpdateWindow(annos);
        }*/
    }
}
