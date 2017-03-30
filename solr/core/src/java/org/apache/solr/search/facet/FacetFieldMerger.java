begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_comment
comment|// TODO: refactor more out to base class
end_comment

begin_class
DECL|class|FacetFieldMerger
specifier|public
class|class
name|FacetFieldMerger
extends|extends
name|FacetRequestSortedMerger
argument_list|<
name|FacetField
argument_list|>
block|{
DECL|field|missingBucket
name|FacetBucket
name|missingBucket
decl_stmt|;
DECL|field|allBuckets
name|FacetBucket
name|allBuckets
decl_stmt|;
DECL|field|numBuckets
name|FacetMerger
name|numBuckets
decl_stmt|;
DECL|field|numReturnedPerShard
name|int
index|[]
name|numReturnedPerShard
decl_stmt|;
comment|// LinkedHashMap<Object,FacetBucket> buckets = new LinkedHashMap<>();
comment|// List<FacetBucket> sortedBuckets;
DECL|field|numReturnedBuckets
name|int
name|numReturnedBuckets
decl_stmt|;
comment|// the number of buckets in the bucket lists returned from all of the shards
DECL|method|FacetFieldMerger
specifier|public
name|FacetFieldMerger
parameter_list|(
name|FacetField
name|freq
parameter_list|)
block|{
name|super
argument_list|(
name|freq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|,
name|Context
name|mcontext
parameter_list|)
block|{
if|if
condition|(
name|numReturnedPerShard
operator|==
literal|null
condition|)
block|{
name|numReturnedPerShard
operator|=
operator|new
name|int
index|[
name|mcontext
operator|.
name|numShards
index|]
expr_stmt|;
block|}
name|merge
argument_list|(
operator|(
name|SimpleOrderedMap
operator|)
name|facetResult
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|SimpleOrderedMap
name|facetResult
parameter_list|,
name|Context
name|mcontext
parameter_list|)
block|{
if|if
condition|(
name|freq
operator|.
name|missing
condition|)
block|{
name|Object
name|o
init|=
name|facetResult
operator|.
name|get
argument_list|(
literal|"missing"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|missingBucket
operator|==
literal|null
condition|)
block|{
name|missingBucket
operator|=
name|newBucket
argument_list|(
literal|null
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
name|missingBucket
operator|.
name|mergeBucket
argument_list|(
operator|(
name|SimpleOrderedMap
operator|)
name|o
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|freq
operator|.
name|allBuckets
condition|)
block|{
name|Object
name|o
init|=
name|facetResult
operator|.
name|get
argument_list|(
literal|"allBuckets"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|allBuckets
operator|==
literal|null
condition|)
block|{
name|allBuckets
operator|=
name|newBucket
argument_list|(
literal|null
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
name|allBuckets
operator|.
name|mergeBucket
argument_list|(
operator|(
name|SimpleOrderedMap
operator|)
name|o
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
name|bucketList
init|=
operator|(
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
operator|)
name|facetResult
operator|.
name|get
argument_list|(
literal|"buckets"
argument_list|)
decl_stmt|;
name|numReturnedPerShard
index|[
name|mcontext
operator|.
name|shardNum
index|]
operator|=
name|bucketList
operator|.
name|size
argument_list|()
expr_stmt|;
name|numReturnedBuckets
operator|+=
name|bucketList
operator|.
name|size
argument_list|()
expr_stmt|;
name|mergeBucketList
argument_list|(
name|bucketList
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
if|if
condition|(
name|freq
operator|.
name|numBuckets
condition|)
block|{
name|Object
name|nb
init|=
name|facetResult
operator|.
name|get
argument_list|(
literal|"numBuckets"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nb
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|numBuckets
operator|==
literal|null
condition|)
block|{
name|numBuckets
operator|=
operator|new
name|FacetNumBucketsMerger
argument_list|()
expr_stmt|;
block|}
name|numBuckets
operator|.
name|merge
argument_list|(
name|nb
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
name|SimpleOrderedMap
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|numBuckets
operator|!=
literal|null
condition|)
block|{
name|int
name|removed
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|freq
operator|.
name|mincount
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|FacetBucket
name|bucket
range|:
name|buckets
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|bucket
operator|.
name|count
operator|<
name|freq
operator|.
name|mincount
condition|)
name|removed
operator|++
expr_stmt|;
block|}
block|}
name|result
operator|.
name|add
argument_list|(
literal|"numBuckets"
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|numBuckets
operator|.
name|getMergedResult
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
operator|-
name|removed
argument_list|)
expr_stmt|;
comment|// TODO: we can further increase this estimate.
comment|// If not sorting by count, use a simple ratio to scale
comment|// If sorting by count desc, then add up the highest_possible_missing_count from each shard
block|}
name|sortBuckets
argument_list|()
expr_stmt|;
name|long
name|first
init|=
name|freq
operator|.
name|offset
decl_stmt|;
name|long
name|end
init|=
name|freq
operator|.
name|limit
operator|>=
literal|0
condition|?
name|first
operator|+
operator|(
name|int
operator|)
name|freq
operator|.
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|last
init|=
name|Math
operator|.
name|min
argument_list|(
name|sortedBuckets
operator|.
name|size
argument_list|()
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|>
name|resultBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
call|(
name|int
call|)
argument_list|(
name|last
operator|-
name|first
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/** this only works if there are no filters (like mincount)     for (int i=first; i<last; i++) {       FacetBucket bucket = sortedBuckets.get(i);       resultBuckets.add( bucket.getMergedBucket() );     }     ***/
comment|// TODO: change effective offsets + limits at shards...
name|int
name|off
init|=
operator|(
name|int
operator|)
name|freq
operator|.
name|offset
decl_stmt|;
name|int
name|lim
init|=
name|freq
operator|.
name|limit
operator|>=
literal|0
condition|?
operator|(
name|int
operator|)
name|freq
operator|.
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|FacetBucket
name|bucket
range|:
name|sortedBuckets
control|)
block|{
if|if
condition|(
name|bucket
operator|.
name|getCount
argument_list|()
operator|<
name|freq
operator|.
name|mincount
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|off
operator|>
literal|0
condition|)
block|{
operator|--
name|off
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|resultBuckets
operator|.
name|size
argument_list|()
operator|>=
name|lim
condition|)
block|{
break|break;
block|}
name|resultBuckets
operator|.
name|add
argument_list|(
name|bucket
operator|.
name|getMergedBucket
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
literal|"buckets"
argument_list|,
name|resultBuckets
argument_list|)
expr_stmt|;
if|if
condition|(
name|missingBucket
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|"missing"
argument_list|,
name|missingBucket
operator|.
name|getMergedBucket
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allBuckets
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|"allBuckets"
argument_list|,
name|allBuckets
operator|.
name|getMergedBucket
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|Context
name|mcontext
parameter_list|)
block|{
comment|// TODO: check refine of subs?
comment|// TODO: call subs each time with a shard/shardnum that is missing a bucket at this level?
comment|// or pass a bit vector of shards w/ value???
comment|// build up data structure and only then call the context (or whatever) to do the refinement?
comment|// basically , only do at the top-level facet?
block|}
annotation|@
name|Override
DECL|method|getRefinementSpecial
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRefinementSpecial
parameter_list|(
name|Context
name|mcontext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|refinement
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|tagsWithPartial
parameter_list|)
block|{
if|if
condition|(
operator|!
name|tagsWithPartial
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Since special buckets missing and allBuckets themselves will always be included, we only need to worry about subfacets being partial.
if|if
condition|(
name|freq
operator|.
name|missing
condition|)
block|{
name|refinement
operator|=
name|getRefinementSpecial
argument_list|(
name|mcontext
argument_list|,
name|refinement
argument_list|,
name|tagsWithPartial
argument_list|,
name|missingBucket
argument_list|,
literal|"missing"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|freq
operator|.
name|allBuckets
condition|)
block|{
name|refinement
operator|=
name|getRefinementSpecial
argument_list|(
name|mcontext
argument_list|,
name|refinement
argument_list|,
name|tagsWithPartial
argument_list|,
name|allBuckets
argument_list|,
literal|"allBuckets"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|refinement
return|;
block|}
DECL|method|getRefinementSpecial
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRefinementSpecial
parameter_list|(
name|Context
name|mcontext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|refinement
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|tagsWithPartial
parameter_list|,
name|FacetBucket
name|bucket
parameter_list|,
name|String
name|label
parameter_list|)
block|{
comment|// boolean prev = mcontext.setBucketWasMissing(true); // the special buckets should have the same "missing" status as this facet, so no need to set it again
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|bucketRefinement
init|=
name|bucket
operator|.
name|getRefinement
argument_list|(
name|mcontext
argument_list|,
name|tagsWithPartial
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketRefinement
operator|!=
literal|null
condition|)
block|{
name|refinement
operator|=
name|refinement
operator|==
literal|null
condition|?
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|2
argument_list|)
else|:
name|refinement
expr_stmt|;
name|refinement
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|bucketRefinement
argument_list|)
expr_stmt|;
block|}
return|return
name|refinement
return|;
block|}
DECL|class|FacetNumBucketsMerger
specifier|private
specifier|static
class|class
name|FacetNumBucketsMerger
extends|extends
name|FacetMerger
block|{
DECL|field|sumBuckets
name|long
name|sumBuckets
decl_stmt|;
DECL|field|shardsMissingSum
name|long
name|shardsMissingSum
decl_stmt|;
DECL|field|shardsTruncatedSum
name|long
name|shardsTruncatedSum
decl_stmt|;
DECL|field|values
name|Set
argument_list|<
name|Object
argument_list|>
name|values
decl_stmt|;
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|,
name|Context
name|mcontext
parameter_list|)
block|{
name|SimpleOrderedMap
name|map
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|facetResult
decl_stmt|;
name|long
name|numBuckets
init|=
operator|(
operator|(
name|Number
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"numBuckets"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|sumBuckets
operator|+=
name|numBuckets
expr_stmt|;
name|List
name|vals
init|=
operator|(
name|List
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"vals"
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|vals
operator|.
name|size
argument_list|()
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|addAll
argument_list|(
name|vals
argument_list|)
expr_stmt|;
if|if
condition|(
name|numBuckets
operator|>
name|values
operator|.
name|size
argument_list|()
condition|)
block|{
name|shardsTruncatedSum
operator|+=
name|numBuckets
operator|-
name|values
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|shardsMissingSum
operator|+=
name|numBuckets
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|Context
name|mcontext
parameter_list|)
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
name|long
name|exactCount
init|=
name|values
operator|==
literal|null
condition|?
literal|0
else|:
name|values
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|exactCount
operator|+
name|shardsMissingSum
operator|+
name|shardsTruncatedSum
return|;
comment|// TODO: reduce count by (at least) number of buckets that fail to hit mincount (after merging)
comment|// that should make things match for most of the small tests at least
block|}
block|}
block|}
end_class

end_unit

