package jp.recochoku.jenkins.plugin.watch;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Date;

/**
 * Sample {@link Publisher}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link AmazonCloudWatchPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #firstName})
 * to remember the configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked.
 *
 * @author Yuto Matsuki
 */
public class AmazonCloudWatchPublisher extends Publisher {

  private final String region;
  private final String namespace;
  private final String metricName;

  @DataBoundConstructor
  public AmazonCloudWatchPublisher(String region, String namespace, String metricName) {
    this.region = region;
    this.namespace = namespace;
    this.metricName = metricName;
  }

  public String getRegion() {
    return region;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getMetricName() {
    return metricName;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    String region = StringUtils.defaultIfBlank(getRegion(), getDescriptor().getRegion());
    String namespace = StringUtils.defaultIfBlank(getNamespace(), getDescriptor().getNamespace());
    String metricName = StringUtils.defaultIfBlank(getMetricName(), getDescriptor().getMetricName());

    // put custom metrics
    listener.getLogger().println("RegionName: " + region);
    listener.getLogger().println("Namespace: " + namespace);
    listener.getLogger().println("MetricName: " + getMetricName());
    AWSCredentialsProvider cp = new DefaultAWSCredentialsProviderChain();
    AmazonCloudWatch cloudWatch = new AmazonCloudWatchClient(cp);
    cloudWatch.setRegion(Region.getRegion(Regions.fromName(region)));
    Date timestamp = new Date();
    StandardUnit unit = StandardUnit.Milliseconds;
    long value = timestamp.getTime() - build.getStartTimeInMillis();
    MetricDatum metricData = new MetricDatum().withMetricName(metricName)
        .withTimestamp(timestamp).withUnit(unit).withValue((double) value);
    cloudWatch.putMetricData(new PutMetricDataRequest()
        .withNamespace(namespace).withMetricData(metricData));
    listener.getLogger().println("Metric data: " + value + "ms");
    return true;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.STEP;
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  @Extension
  public static final class DescriptorImpl extends Descriptor<Publisher> {

    private String region;
    private String namespace;
    private String metricName;

    public DescriptorImpl() {
      load();
    }

    public String getDisplayName() {
      return "Amazon CloudWatch Publisher";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      region = formData.getString("region");
      namespace = formData.getString("namespace");
      metricName = formData.getString("metricName");
      save();
      return super.configure(req, formData);
    }

    public String getRegion() {
      return region;
    }

    public String getNamespace() {
      return namespace;
    }

    public String getMetricName() {
      return metricName;
    }
  }

}

