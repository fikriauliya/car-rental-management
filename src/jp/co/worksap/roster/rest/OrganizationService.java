package jp.co.worksap.roster.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import jp.co.worksap.roster.ejb.OrganizationEJB;
import jp.co.worksap.roster.entity.OrganizationUnit;
import jp.co.worksap.roster.entity.OrganizationUnitTree;
import jp.co.worksap.roster.rest.modelview.OrganizationHierarchy;

@Path("/organizations")
@Stateless
public class OrganizationService {
	@EJB
	private OrganizationEJB organizationEJB;

	@POST
	public void createOrganization() {
		OrganizationUnit newOrg = new OrganizationUnit();
		newOrg.setName("Singapore Office");
		newOrg.setDescription("WORKS Singapore 3, One North");
		organizationEJB.createOrganization(newOrg, 351);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public OrganizationHierarchy getOrganizationHierarchy() {
		List<OrganizationUnitTree> tree = organizationEJB.getOrganizationStucture();

		Map<OrganizationUnit, OrganizationHierarchy> m = new HashMap<OrganizationUnit, OrganizationHierarchy>();

		Set<OrganizationUnit> rootCandidates = new HashSet<OrganizationUnit>();
		Set<OrganizationUnit> notRoots = new HashSet<OrganizationUnit>();

		for (OrganizationUnitTree t : tree) {
			System.out.println("Item: " + t);

			rootCandidates.add(t.getAncestor());
			notRoots.add(t.getDescendant());

			if (!m.containsKey(t.getAncestor())) {
				OrganizationHierarchy parent = new OrganizationHierarchy();
				parent.setData(t.getAncestor());

				OrganizationHierarchy child = null;
				if (m.containsKey(t.getDescendant())) {
					child = m.get(t.getDescendant());
				} else {
					child = new OrganizationHierarchy();
					child.setData(t.getDescendant());

					m.put(t.getDescendant(), child);
				}

				parent.getChildren().add(child);
				m.put(t.getAncestor(), parent);
			} else {
				OrganizationHierarchy newChild = null;

				if (m.containsKey(t.getDescendant())) {
					newChild = m.get(t.getDescendant());

				} else {
					newChild = new OrganizationHierarchy();
					newChild.setData(t.getDescendant());

					m.put(t.getDescendant(), newChild);
				}
				m.get(t.getAncestor()).getChildren().add(newChild);
			}
		}

		System.out.println(StringUtils.join(rootCandidates));
		System.out.println(StringUtils.join(notRoots));
		rootCandidates.removeAll(notRoots);
		if (rootCandidates.isEmpty() || rootCandidates.size() != 1) {
			throw new RuntimeException("Broken tree");
		}

		OrganizationUnit root = rootCandidates.toArray(new OrganizationUnit[0])[0];
		return m.get(root);
	}
}
