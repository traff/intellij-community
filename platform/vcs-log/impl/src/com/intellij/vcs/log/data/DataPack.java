package com.intellij.vcs.log.data;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.vcs.log.*;
import com.intellij.vcs.log.graph.*;
import com.intellij.vcs.log.newgraph.facade.GraphFacadeImpl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

public class DataPack {
  private static final boolean USE_NEW_FACADE = true;


  @NotNull private final RefsModel myRefsModel;
  @NotNull private final GraphFacade myGraphFacade;

  @NotNull
  public static DataPack build(@NotNull List<? extends GraphCommit> commits,
                               @NotNull Collection<VcsRef> allRefs,
                               @NotNull ProgressIndicator indicator,
                               @NotNull NotNullFunction<Hash, Integer> indexGetter,
                               @NotNull NotNullFunction<Integer, Hash> hashGetter, 
                               @NotNull Map<VirtualFile, VcsLogProvider> logProviders) {
    indicator.setText("Building graph...");
    final RefsModel refsModel = new RefsModel(allRefs, indexGetter);
    GraphColorManagerImpl colorManager = new GraphColorManagerImpl(refsModel, hashGetter, getRefManagerMap(logProviders));
    if (USE_NEW_FACADE) {
      if (!commits.isEmpty()) {
        return new DataPack(refsModel, GraphFacadeImpl.newInstance(commits, getBranchCommitHashIndexes(allRefs, indexGetter), colorManager));
      }
      else {
        return new DataPack(refsModel, new EmptyGraphFacade());
      }
    } else {
      GraphFacade graphFacade = new GraphFacadeBuilderImpl().build(commits, refsModel, colorManager);
      return new DataPack(refsModel, graphFacade);
    }
  }

  @NotNull
  private static Set<Integer> getBranchCommitHashIndexes(@NotNull Collection<VcsRef> allRefs,
                                                         @NotNull NotNullFunction<Hash, Integer> indexGetter) {
    Set<Integer> result = new HashSet<Integer>();
    for (VcsRef vcsRef : allRefs) {
      if (vcsRef.getType().isBranch())
        result.add(indexGetter.fun(vcsRef.getCommitHash()));
    }
    return result;
  }

  @NotNull
  private static Map<VirtualFile, VcsLogRefManager> getRefManagerMap(@NotNull Map<VirtualFile, VcsLogProvider> logProviders) {
    Map<VirtualFile, VcsLogRefManager> map = ContainerUtil.newHashMap();
    for (Map.Entry<VirtualFile, VcsLogProvider> entry : logProviders.entrySet()) {
      map.put(entry.getKey(), entry.getValue().getReferenceManager());
    }
    return map;
  }

  private DataPack(@NotNull RefsModel refsModel, @NotNull GraphFacade graphFacade) {
    myRefsModel = refsModel;
    myGraphFacade = graphFacade;
  }

  @NotNull
  public GraphFacade getGraphFacade() {
    return myGraphFacade;
  }

  @NotNull
  public RefsModel getRefsModel() {
    return myRefsModel;
  }

}
