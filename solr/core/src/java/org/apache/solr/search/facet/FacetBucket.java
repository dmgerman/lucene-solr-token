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
name|Map
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

begin_class
DECL|class|FacetBucket
specifier|public
class|class
name|FacetBucket
block|{
DECL|field|parent
specifier|final
name|FacetBucketMerger
name|parent
decl_stmt|;
DECL|field|bucketValue
specifier|final
name|Comparable
name|bucketValue
decl_stmt|;
DECL|field|bucketNumber
specifier|final
name|int
name|bucketNumber
decl_stmt|;
comment|// this is just for internal correlation (the first bucket created is bucket 0, the next bucket 1, across all field buckets)
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|subs
name|Map
argument_list|<
name|String
argument_list|,
name|FacetMerger
argument_list|>
name|subs
decl_stmt|;
DECL|method|FacetBucket
specifier|public
name|FacetBucket
parameter_list|(
name|FacetBucketMerger
name|parent
parameter_list|,
name|Comparable
name|bucketValue
parameter_list|,
name|FacetMerger
operator|.
name|Context
name|mcontext
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|bucketValue
operator|=
name|bucketValue
expr_stmt|;
name|this
operator|.
name|bucketNumber
operator|=
name|mcontext
operator|.
name|getNewBucketNumber
argument_list|()
expr_stmt|;
comment|// TODO: we don't need bucket numbers for all buckets...
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/** returns the existing merger for the given key, or null if none yet exists */
DECL|method|getExistingMerger
name|FacetMerger
name|getExistingMerger
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|subs
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getMerger
specifier|private
name|FacetMerger
name|getMerger
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|prototype
parameter_list|)
block|{
name|FacetMerger
name|merger
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
condition|)
block|{
name|merger
operator|=
name|subs
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|merger
operator|!=
literal|null
condition|)
return|return
name|merger
return|;
block|}
name|merger
operator|=
name|parent
operator|.
name|createFacetMerger
argument_list|(
name|key
argument_list|,
name|prototype
argument_list|)
expr_stmt|;
if|if
condition|(
name|merger
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
block|{
name|subs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|subs
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|merger
argument_list|)
expr_stmt|;
block|}
return|return
name|merger
return|;
block|}
DECL|method|mergeBucket
specifier|public
name|void
name|mergeBucket
parameter_list|(
name|SimpleOrderedMap
name|bucket
parameter_list|,
name|FacetMerger
operator|.
name|Context
name|mcontext
parameter_list|)
block|{
comment|// todo: for refinements, we want to recurse, but not re-do stats for intermediate buckets
name|mcontext
operator|.
name|setShardFlag
argument_list|(
name|bucketNumber
argument_list|)
expr_stmt|;
comment|// drive merging off the received bucket?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bucket
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|bucket
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
name|bucket
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|count
operator|+=
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
literal|"val"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// this is taken care of at a higher level...
continue|continue;
block|}
name|FacetMerger
name|merger
init|=
name|getMerger
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|merger
operator|!=
literal|null
condition|)
block|{
name|merger
operator|.
name|merge
argument_list|(
name|val
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getMergedBucket
specifier|public
name|SimpleOrderedMap
name|getMergedBucket
parameter_list|()
block|{
name|SimpleOrderedMap
name|out
init|=
operator|new
name|SimpleOrderedMap
argument_list|(
operator|(
name|subs
operator|==
literal|null
condition|?
literal|0
else|:
name|subs
operator|.
name|size
argument_list|()
operator|)
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketValue
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
literal|"val"
argument_list|,
name|bucketValue
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FacetMerger
argument_list|>
name|mergerEntry
range|:
name|subs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FacetMerger
name|subMerger
init|=
name|mergerEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|add
argument_list|(
name|mergerEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|subMerger
operator|.
name|getMergedResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
DECL|method|getRefinement
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRefinement
parameter_list|(
name|FacetMerger
operator|.
name|Context
name|mcontext
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|refineTags
parameter_list|)
block|{
if|if
condition|(
name|subs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|refinement
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|refineTags
control|)
block|{
name|FacetMerger
name|subMerger
init|=
name|subs
operator|.
name|get
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|subMerger
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|subRef
init|=
name|subMerger
operator|.
name|getRefinement
argument_list|(
name|mcontext
argument_list|)
decl_stmt|;
if|if
condition|(
name|subRef
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|refinement
operator|==
literal|null
condition|)
block|{
name|refinement
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|refineTags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|refinement
operator|.
name|put
argument_list|(
name|tag
argument_list|,
name|subRef
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|refinement
return|;
block|}
DECL|method|getRefinement2
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRefinement2
parameter_list|(
name|FacetMerger
operator|.
name|Context
name|mcontext
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|refineTags
parameter_list|)
block|{
comment|// TODO - partial results should turn off refining!!!
name|boolean
name|parentMissing
init|=
name|mcontext
operator|.
name|bucketWasMissing
argument_list|()
decl_stmt|;
comment|// TODO: this is a redundant check for many types of facets... only do on field faceting
if|if
condition|(
operator|!
name|parentMissing
condition|)
block|{
comment|// if parent bucket wasn't missing, check if this bucket was.
comment|// this really only needs checking on certain buckets... (like terms facet)
name|boolean
name|sawThisBucket
init|=
name|mcontext
operator|.
name|getShardFlag
argument_list|(
name|bucketNumber
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sawThisBucket
condition|)
block|{
name|mcontext
operator|.
name|setBucketWasMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if parent bucket was missing, then we should be too
assert|assert
operator|!
name|mcontext
operator|.
name|getShardFlag
argument_list|(
name|bucketNumber
argument_list|)
assert|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|refinement
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|mcontext
operator|.
name|bucketWasMissing
argument_list|()
condition|)
block|{
comment|// this is just a pass-through bucket... see if there is anything to do at all
if|if
condition|(
name|subs
operator|==
literal|null
operator|||
name|refineTags
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|// for missing bucket, go over all sub-facts
name|refineTags
operator|=
literal|null
expr_stmt|;
name|refinement
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
name|bucketValue
operator|!=
literal|null
condition|)
block|{
name|refinement
operator|.
name|put
argument_list|(
literal|"_v"
argument_list|,
name|bucketValue
argument_list|)
expr_stmt|;
block|}
name|refinement
operator|.
name|put
argument_list|(
literal|"_m"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// TODO: listing things like sub-facets that have no field facets are redundant
comment|// (we only need facet that have variable values)
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FacetMerger
argument_list|>
name|sub
range|:
name|subs
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|refineTags
operator|!=
literal|null
operator|&&
operator|!
name|refineTags
operator|.
name|contains
argument_list|(
name|sub
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|subRef
init|=
name|sub
operator|.
name|getValue
argument_list|()
operator|.
name|getRefinement
argument_list|(
name|mcontext
argument_list|)
decl_stmt|;
if|if
condition|(
name|subRef
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|refinement
operator|==
literal|null
condition|)
block|{
name|refinement
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
name|refinement
operator|.
name|put
argument_list|(
name|sub
operator|.
name|getKey
argument_list|()
argument_list|,
name|subRef
argument_list|)
expr_stmt|;
block|}
block|}
comment|// reset the "bucketMissing" flag on the way back out.
name|mcontext
operator|.
name|setBucketWasMissing
argument_list|(
name|parentMissing
argument_list|)
expr_stmt|;
return|return
name|refinement
return|;
block|}
block|}
end_class

end_unit
