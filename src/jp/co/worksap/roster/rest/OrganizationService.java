package jp.co.worksap.roster.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.OrganizationEJB;
import jp.co.worksap.roster.entity.OrganizationUnit;
import jp.co.worksap.roster.entity.OrganizationUnitTree;
import jp.co.worksap.roster.rest.modelview.OrganizationHierarchy;
import jp.co.worksap.roster.rest.modelview.OrganizationUnitWithParent;

@Path("/organizations")
@Stateless
public class OrganizationService {
	@EJB
	private OrganizationEJB organizationEJB;

	@POST
	public void createOrganization(OrganizationUnitWithParent newOrg) {
		organizationEJB.createOrganization(newOrg.toOrganizationUnit(), newOrg.getParentId());
	}

	@DELETE
	public void deleteOrganization(@QueryParam("id") int id) {
		organizationEJB.deleteOrganization(id);
	}

	@PUT
	public void updateOrganization(OrganizationUnit org) {
		organizationEJB.updateOrganization(org);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object getOrganizationHierarchy() {
		List<OrganizationUnitTree> tree = organizationEJB.getOrganizationStucture();

		Map<OrganizationUnit, OrganizationHierarchy> m = new HashMap<OrganizationUnit, OrganizationHierarchy>();

		Set<OrganizationUnit> rootCandidates = new HashSet<OrganizationUnit>();
		Set<OrganizationUnit> notRoots = new HashSet<OrganizationUnit>();

		for (OrganizationUnitTree t : tree) {
			if (t.getLength() == 0) {
				rootCandidates.add(t.getAncestor());
				if (!m.containsKey(t.getAncestor())) {
					OrganizationHierarchy parent = new OrganizationHierarchy();
					parent.setData(t.getAncestor());
					m.put(t.getAncestor(), parent);
				}
			}

			if (t.getLength() > 0) {
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
		}

		rootCandidates.removeAll(notRoots);
		if (rootCandidates.isEmpty()) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
		}
		if (rootCandidates.size() != 1) {
			throw new RuntimeException("Broken tree");
		}

		OrganizationUnit root = rootCandidates.toArray(new OrganizationUnit[0])[0];
		return m.get(root);
	}
}
