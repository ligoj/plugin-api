package org.ligoj.app.resource.project;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.ligoj.app.api.NodeStatus;
import org.ligoj.app.iam.UserOrg;
import org.ligoj.app.model.ParameterValue;
import org.ligoj.app.model.Project;
import org.ligoj.app.model.Subscription;
import org.ligoj.app.resource.node.EventVo;
import org.ligoj.app.resource.node.NodeResource;
import org.ligoj.app.resource.node.ParameterValueResource;
import org.ligoj.app.resource.node.ParameterValueVo;
import org.ligoj.app.resource.subscription.SubscriptionVo;
import org.ligoj.bootstrap.core.DescribedBean;

/**
 * JPA {@link Project} to detailed {@link ProjectVo} converter.
 */
class ToVoConverter implements Function<Project, ProjectVo> {

	/**
	 * Subscriptions.
	 */
	private List<Object[]> subscriptionsAndParam;

	/**
	 * Subscriptions status
	 */
	private Map<Integer, EventVo> subscriptionStatus;

	/**
	 * User converter used to serialize a safe data.
	 */
	private Function<String, ? extends UserOrg> userConverter;

	/**
	 * Constructor holding the data used to convert a {@link Project} to {@link ProjectVo}.
	 * 
	 * @param userConverter
	 *            The {@link Function} used to convert internal user identifier to described user.
	 * @param subscriptionsAndParam
	 *            The subscription (index 0, type {@link Subscription}) with parameter values (index 1, type
	 *            {@link ParameterValue}).
	 * @param subscriptionStatus
	 *            The subscriptions statuses. Key is the subscription identifier.
	 */
	protected ToVoConverter(final Function<String, ? extends UserOrg> userConverter, final List<Object[]> subscriptionsAndParam,
			final Map<Integer, EventVo> subscriptionStatus) {
		this.subscriptionsAndParam = subscriptionsAndParam;
		this.subscriptionStatus = subscriptionStatus;
		this.userConverter = userConverter;
	}

	@Override
	public ProjectVo apply(final Project entity) {
		final ProjectVo vo = new ProjectVo();
		vo.copyAuditData(entity, userConverter);
		DescribedBean.copy(entity, vo);
		vo.setPkey(entity.getPkey());
		vo.setTeamLeader(userConverter.apply(entity.getTeamLeader()));

		// Build the subscriptions
		final Map<Integer, SubscriptionVo> subscriptions = new LinkedHashMap<>();
		for (final Object[] resultSet : this.subscriptionsAndParam) {
			final Subscription subscriptionEntity = (Subscription) resultSet[0];
			SubscriptionVo subscriptionVo = subscriptions.get(subscriptionEntity.getId());

			// Build the subscription root instance
			if (subscriptionVo == null) {
				subscriptionVo = new SubscriptionVo();
				subscriptionVo.copyAuditData(subscriptionEntity, userConverter);
				subscriptionVo.setId(subscriptionEntity.getId());
				subscriptionVo.setNode(NodeResource.toVo(subscriptionEntity.getNode()));
				subscriptionVo.setParameters(new HashMap<>());
				subscriptions.put(subscriptionEntity.getId(), subscriptionVo);

				// Add subscription status
				final EventVo lastEvent = subscriptionStatus.get(subscriptionEntity.getId());
				if (lastEvent != null) {
					subscriptionVo.setStatus(NodeStatus.valueOf(lastEvent.getValue()));
				}
			}

			// Add subscription value
			final ParameterValue parameterValue = (ParameterValue) resultSet[1];
			subscriptionVo.getParameters().put(parameterValue.getParameter().getId(),
					ParameterValueResource.parseValue(parameterValue, new ParameterValueVo()));

		}
		vo.setSubscriptions(subscriptions.values());
		return vo;
	}
}
