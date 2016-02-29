package com.mjrichardson.teamCity.buildTriggers.Fakes;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.BuildAgent;
import jetbrains.buildServer.BuildTypeDescriptor;
import jetbrains.buildServer.BuildTypeStatusDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.parameters.ParametersProvider;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.artifacts.SArtifactDependency;
import jetbrains.buildServer.serverSide.comments.Comment;
import jetbrains.buildServer.serverSide.dependency.CyclicDependencyFoundException;
import jetbrains.buildServer.serverSide.dependency.Dependency;
import jetbrains.buildServer.serverSide.dependency.Dependent;
import jetbrains.buildServer.serverSide.identifiers.DuplicateExternalIdException;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.Option;
import jetbrains.buildServer.vcs.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


class FakeSBuildType implements SBuildType {
    @Override
    public List<SFinishedBuild> getHistory() {
        return null;
    }

    @Override
    public List<SFinishedBuild> getHistoryFull(boolean b) {
        return null;
    }

    @Override
    public List<SFinishedBuild> getHistory(@Nullable User user, boolean b, boolean b1) {
        return null;
    }

    @Nullable
    @Override
    public SBuild getLastChangesStartedBuild() {
        return null;
    }

    @Override
    public List<AgentCompatibility> getAgentCompatibilities() {
        return null;
    }

    @Override
    public <T extends SBuildAgent> AgentCompatibility getAgentCompatibility(@NotNull T t) {
        return null;
    }

    @Override
    public <T extends BuildAgent> List<T> getCanRunAgents() {
        return null;
    }

    @Override
    public <T extends BuildAgent> List<T> getCanRunAndCompatibleAgents(boolean b) {
        return null;
    }

    @Override
    public BuildNumbers getBuildNumbers() {
        return null;
    }

    @NotNull
    @Override
    public List<SRunningBuild> getRunningBuilds(@Nullable User user) {
        return null;
    }

    @NotNull
    @Override
    public List<SRunningBuild> getRunningBuilds() {
        return null;
    }

    @Nullable
    @Override
    public SFinishedBuild getLastChangesFinished() {
        return null;
    }

    @Nullable
    @Override
    public SFinishedBuild getLastChangesSuccessfullyFinished() {
        return null;
    }

    @Override
    public void setPaused(boolean b, User user) {

    }

    @Override
    public void setPaused(boolean b, @Nullable User user, String s) {

    }

    @Nullable
    @Override
    public Comment getPauseComment() {
        return null;
    }

    @NotNull
    @Override
    public SProject getProject() {
        return null;
    }

    @Override
    public void setName(@NotNull String s) {

    }

    @Override
    public void setDescription(@Nullable String s) {

    }

    @Override
    public List<SBuildType> getArtifactsReferences() {
        return null;
    }

    @Override
    public int getNumberOfArtifactReferences() {
        return 0;
    }

    @NotNull
    @Override
    public File getArtifactsDirectory() {
        return null;
    }

    @Override
    public List<SVcsModification> getPendingChanges() {
        return null;
    }

    @Override
    public Collection<SUser> getPendingChangesCommitters() {
        return null;
    }

    @Override
    public List<SVcsModification> getModificationsSinceLastSuccessful() {
        return null;
    }

    @NotNull
    @Override
    public String getVcsSettingsHash(@NotNull List<VcsRootInstanceEntry> list) {
        return null;
    }

    @NotNull
    @Override
    public String getVcsSettingsHash() {
        return null;
    }

    @Override
    public void releaseSources() {

    }

    @Override
    public void releaseSources(@NotNull SBuildAgent sBuildAgent) {

    }

    @NotNull
    @Override
    public List<SBuildAgent> getAgentsWhereBuildConfigurationBuilt() {
        return null;
    }

    @Override
    public List<String> getTags() {
        return null;
    }

    @Override
    public void forceCheckingForChanges() {

    }

    @Nullable
    @Override
    public SQueuedBuild addToQueue(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public SQueuedBuild addToQueue(@NotNull BuildAgent buildAgent, @NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public List<SQueuedBuild> getQueuedBuilds(@Nullable User user) {
        return null;
    }

    @Override
    public boolean isCleanBuild() {
        return false;
    }

    @Override
    public boolean isShouldFailBuildIfTestsFailed() {
        return false;
    }

    @Override
    public int getExecutionTimeoutMin() {
        return 0;
    }

    @Override
    public int getMaximumNumberOfBuilds() {
        return 0;
    }

    @Override
    public boolean isAllowExternalStatus() {
        return false;
    }

    @Override
    public void moveToProject(@NotNull SProject sProject, boolean b) throws InvalidVcsRootScopeException {

    }

    @Override
    public void moveToProject(@NotNull SProject sProject) throws InvalidVcsRootScopeException {

    }

    @Override
    public void moveToProject(@NotNull ConfigAction configAction, @NotNull SProject sProject) throws InvalidVcsRootScopeException {

    }

    @Nullable
    @Override
    public PathMapping mapVcsPath(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public String getExtendedName() {
        return null;
    }

    @NotNull
    @Override
    public String getExtendedFullName() {
        return null;
    }

    @NotNull
    @Override
    public byte[] getFileContent(@NotNull String s) throws VcsException {
        return new byte[0];
    }

    @Override
    public void attachToTemplate(@NotNull BuildTypeTemplate buildTypeTemplate, boolean b) throws InvalidVcsRootScopeException, CannotAttachToTemplateException {

    }

    @Override
    public void attachToTemplate(@NotNull BuildTypeTemplate buildTypeTemplate) throws CannotAttachToTemplateException {

    }

    @Override
    public void detachFromTemplate() {

    }

    @Override
    public void persist() {

    }

    @NotNull
    @Override
    public ResolvedSettings getResolvedSettings() {
        return null;
    }

    @NotNull
    @Override
    public List<Dependency> getOwnDependencies() {
        return null;
    }

    @NotNull
    @Override
    public CustomDataStorage getCustomDataStorage(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public SBuildRunnerDescriptor findBuildRunnerByType(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public List<VcsRootInstanceEntry> getVcsRootInstanceEntries() {
        return null;
    }

    @Nullable
    @Override
    public VcsRootInstance getVcsRootInstanceForParent(@NotNull SVcsRoot sVcsRoot) {
        return null;
    }

    @NotNull
    @Override
    public List<VcsRootInstance> getVcsRootInstances() {
        return null;
    }

    @Override
    public boolean belongsTo(@NotNull SProject sProject) {
        return false;
    }

    @NotNull
    @Override
    public List<VcsRootEntry> getOwnVcsRootEntries() {
        return null;
    }

    @NotNull
    @Override
    public List<SBuildType> getDependencyReferences() {
        return null;
    }

    @Override
    public int getNumberOfDependencyReferences() {
        return 0;
    }

    @Override
    public boolean isInQueue() {
        return false;
    }

    @Override
    public int getNumberQueued() {
        return 0;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @NotNull
    @Override
    public BuildTypeStatusDescriptor getStatusDescriptor() {
        return null;
    }

    @NotNull
    @Override
    public ResponsibilityEntry getResponsibilityInfo() {
        return null;
    }

    @Override
    public void setResponsible(@NotNull User user, @Nullable String s, @Nullable User user1) {

    }

    @Override
    public void removeResponsible(boolean b, @Nullable User user, @Nullable String s, @Nullable User user1) {

    }

    @Nullable
    @Override
    public Build getBuildByBuildNumber(@NotNull String s) {
        return null;
    }

    @Override
    public String getBuildParameter(String s) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getBuildParameters() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @NotNull
    @Override
    public String getBuildTypeId() {
        return null;
    }

    @NotNull
    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Collection<String> getRunnerTypes() {
        return null;
    }

    @Override
    public boolean isPersonal() {
        return false;
    }

    @Override
    public CheckoutType getCheckoutType() {
        return null;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @NotNull
    @Override
    public String getProjectId() {
        return null;
    }

    @NotNull
    @Override
    public String getProjectExternalId() {
        return null;
    }

    @NotNull
    @Override
    public String getInternalId() {
        return null;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return null;
    }

    @Override
    public void setExternalId(@NotNull String s) throws InvalidIdentifierException, DuplicateExternalIdException {

    }

    @Override
    public void setExternalId(@NotNull ConfigAction configAction, @NotNull String s) throws InvalidIdentifierException, DuplicateExternalIdException {

    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @NotNull
    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public void remove() {

    }

    @NotNull
    @Override
    public File getConfigurationFile() {
        return null;
    }

    @NotNull
    @Override
    public List<Requirement> getRunTypeRequirements() {
        return null;
    }

    @NotNull
    @Override
    public SBuildRunnerDescriptor addBuildRunner(@NotNull BuildRunnerDescriptor buildRunnerDescriptor) {
        return null;
    }

    @NotNull
    @Override
    public SBuildRunnerDescriptor addBuildRunner(@NotNull String s, @NotNull String s1, @NotNull Map<String, String> map) {
        return null;
    }

    @Nullable
    @Override
    public SBuildRunnerDescriptor removeBuildRunner(@NotNull String s) {
        return null;
    }

    @Override
    public boolean updateBuildRunner(@NotNull String s, @NotNull String s1, @NotNull String s2, @NotNull Map<String, String> map) {
        return false;
    }

    @NotNull
    @Override
    public List<SBuildRunnerDescriptor> getBuildRunners() {
        return null;
    }

    @Override
    public void removeAllBuildRunners() {

    }

    @Override
    public void applyRunnersOrder(@NotNull String[] strings) {

    }

    @Nullable
    @Override
    public SBuildRunnerDescriptor findBuildRunnerById(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public String findRunnerParameter(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public Collection<Parameter> getBuildParametersCollection() {
        return null;
    }

    @Override
    public void addBuildParameter(Parameter parameter) {

    }

    @Override
    public void removeBuildParameter(String s) {

    }

    @NotNull
    @Override
    public List<SVcsRoot> getVcsRoots() {
        return null;
    }

    @Override
    public boolean addBuildTrigger(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        return false;
    }

    @NotNull
    @Override
    public BuildTriggerDescriptor addBuildTrigger(@NotNull String s, @NotNull Map<String, String> map) {
        return null;
    }

    @Override
    public boolean removeBuildTrigger(@NotNull BuildTriggerDescriptor buildTriggerDescriptor) {
        return false;
    }

    @Override
    public boolean updateBuildTrigger(@NotNull String s, @NotNull String s1, @NotNull Map<String, String> map) {
        return false;
    }

    @NotNull
    @Override
    public Collection<BuildTriggerDescriptor> getBuildTriggersCollection() {
        return null;
    }

    @Nullable
    @Override
    public BuildTriggerDescriptor findTriggerById(@NotNull String s) {
        return null;
    }

    @Override
    public void setCheckoutType(CheckoutType checkoutType) {

    }

    @Override
    public void setCheckoutDirectory(@Nullable String s) {

    }

    @Nullable
    @Override
    public String getCheckoutDirectory() {
        return null;
    }

    @Override
    public void setArtifactPaths(@Nullable String s) {

    }

    @Nullable
    @Override
    public String getArtifactPaths() {
        return null;
    }

    @NotNull
    @Override
    public List<SArtifactDependency> getArtifactDependencies() {
        return null;
    }

    @Override
    public void setArtifactDependencies(@NotNull List<SArtifactDependency> list) {

    }

    @Override
    public boolean containsVcsRoot(long l) {
        return false;
    }

    @NotNull
    @Override
    public List<VcsRootEntry> getVcsRootEntries() {
        return null;
    }

    @Override
    public boolean addVcsRoot(@NotNull SVcsRoot sVcsRoot) throws InvalidVcsRootScopeException, VcsRootNotFoundException {
        return false;
    }

    @Override
    public boolean removeVcsRoot(@NotNull SVcsRoot sVcsRoot) {
        return false;
    }

    @Override
    public boolean setCheckoutRules(@NotNull VcsRoot vcsRoot, @NotNull CheckoutRules checkoutRules) {
        return false;
    }

    @Override
    public CheckoutRules getCheckoutRules(@NotNull VcsRoot vcsRoot) {
        return null;
    }

    @Override
    public void addConfigParameter(@NotNull Parameter parameter) {

    }

    @Override
    public void removeConfigParameter(@NotNull String s) {

    }

    @NotNull
    @Override
    public Collection<Parameter> getConfigParametersCollection() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getConfigParameters() {
        return null;
    }

    @NotNull
    @Override
    public List<String> getUndefinedParameters() {
        return null;
    }

    @Override
    public boolean isTemplateBased() {
        return false;
    }

    @Nullable
    @Override
    public BuildTypeTemplate getTemplate() {
        return null;
    }

    @Override
    public boolean isTemplateAccessible() {
        return false;
    }

    @Nullable
    @Override
    public String getTemplateId() {
        return null;
    }

    @NotNull
    @Override
    public List<Requirement> getRequirements() {
        return null;
    }

    @NotNull
    @Override
    public List<Requirement> getImplicitRequirements() {
        return null;
    }

    @Override
    public void addRequirement(@NotNull Requirement requirement) {

    }

    @Override
    public void removeRequirement(String s) {

    }

    @NotNull
    @Override
    public String getBuildNumberPattern() {
        return null;
    }

    @Override
    public void setBuildNumberPattern(@NotNull String s) {

    }

    @Override
    public boolean replaceInValues(@NotNull String s, @NotNull String s1) throws PatternSyntaxException {
        return false;
    }

    @Override
    public boolean replaceInValues(@NotNull Pattern pattern, @NotNull String s) {
        return false;
    }

    @Override
    public boolean textValueMatches(@NotNull Pattern pattern) {
        return false;
    }

    @NotNull
    @Override
    public SBuildFeatureDescriptor addBuildFeature(@NotNull String s, @NotNull Map<String, String> map) {
        return null;
    }

    @Override
    public void addBuildFeature(@NotNull SBuildFeatureDescriptor sBuildFeatureDescriptor) {

    }

    @NotNull
    @Override
    public Collection<SBuildFeatureDescriptor> getBuildFeatures() {
        return null;
    }

    @NotNull
    @Override
    public Collection<SBuildFeatureDescriptor> getBuildFeaturesOfType(@NotNull String s) {
        return null;
    }

    @Nullable
    @Override
    public SBuildFeatureDescriptor removeBuildFeature(@NotNull String s) {
        return null;
    }

    @Override
    public boolean updateBuildFeature(@NotNull String s, @NotNull String s1, @NotNull Map<String, String> map) {
        return false;
    }

    @Nullable
    @Override
    public SBuildFeatureDescriptor findBuildFeatureById(@NotNull String s) {
        return null;
    }

    @Override
    public void setEnabled(@NotNull String s, boolean b) {

    }

    @Override
    public boolean isEnabled(@NotNull String s) {
        return false;
    }

    @Override
    public int compareTo(BuildTypeDescriptor o) {
        return 0;
    }

    @NotNull
    @Override
    public Map<SBuildAgent, CompatibilityResult> getCompatibilityMap() {
        return null;
    }

    @NotNull
    @Override
    public Collection<SBuildAgent> getCompatibleAgents() {
        return null;
    }

    @NotNull
    @Override
    public CompatibilityResult getAgentCompatibility(@NotNull AgentDescription agentDescription) {
        return null;
    }

    @NotNull
    @Override
    public Collection<SBuildType> getChildDependencies() {
        return null;
    }

    @Override
    public boolean intersectsWith(@NotNull Dependent dependent) {
        return false;
    }

    @NotNull
    @Override
    public List<Dependency> getDependencies() {
        return null;
    }

    @Override
    public void addDependency(@NotNull Dependency dependency) throws CyclicDependencyFoundException {

    }

    @Override
    public boolean removeDependency(@NotNull Dependency dependency) {
        return false;
    }

    @NotNull
    @Override
    public Collection<Parameter> getParametersCollection() {
        return null;
    }

    @NotNull
    @Override
    public Collection<Parameter> getOwnParametersCollection() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, String> getOwnParameters() {
        return null;
    }

    @Override
    public <T> void setOption(@NotNull Option<T> option, @NotNull T t) {

    }

    @NotNull
    @Override
    public Collection<Option> getOwnOptions() {
        return null;
    }

    @NotNull
    @Override
    public Collection<Option> getOptions() {
        return null;
    }

    @NotNull
    @Override
    public Option[] getChangedOptions() {
        return new Option[0];
    }

    @NotNull
    @Override
    public <T> T getOption(@NotNull Option<T> option) {
        return null;
    }

    @NotNull
    @Override
    public <T> T getOptionDefaultValue(@NotNull Option<T> option) {
        return null;
    }

    @NotNull
    @Override
    public ParametersProvider getParametersProvider() {
        return null;
    }

    @NotNull
    @Override
    public ValueResolver getValueResolver() {
        return null;
    }

    @Override
    public void persist(@NotNull ConfigAction configAction) {

    }

    @Nullable
    @Override
    public SPersistentEntity getParent() {
        return null;
    }

    @Override
    public void addParameter(@NotNull Parameter parameter) {

    }

    @Override
    public void removeParameter(@NotNull String s) {

    }

    @NotNull
    @Override
    public LabelingType getLabelingType() {
        return null;
    }

    @NotNull
    @Override
    public String getLabelPattern() {
        return null;
    }

    @NotNull
    @Override
    public List<VcsRoot> getLabelingRoots() {
        return null;
    }
}
