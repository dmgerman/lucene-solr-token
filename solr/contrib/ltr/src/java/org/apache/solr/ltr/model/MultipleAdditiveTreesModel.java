begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.model
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|model
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
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
name|ltr
operator|.
name|feature
operator|.
name|Feature
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
name|ltr
operator|.
name|norm
operator|.
name|Normalizer
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
name|util
operator|.
name|SolrPluginUtils
import|;
end_import

begin_comment
comment|/**  * A scoring model that computes scores based on the summation of multiple weighted trees.  * Example models are LambdaMART and Gradient Boosted Regression Trees (GBRT) .  *<p>  * Example configuration:<pre>{    "class" : "org.apache.solr.ltr.model.MultipleAdditiveTreesModel",    "name" : "multipleadditivetreesmodel",    "features":[        { "name" : "userTextTitleMatch"},        { "name" : "originalScore"}    ],    "params" : {        "trees" : [            {                "weight" : 1,                "root": {                    "feature" : "userTextTitleMatch",                    "threshold" : 0.5,                    "left" : {                        "value" : -100                    },                    "right" : {                        "feature" : "originalScore",                        "threshold" : 10.0,                        "left" : {                            "value" : 50                        },                        "right" : {                            "value" : 75                        }                    }                }            },            {                "weight" : 2,                "root" : {                    "value" : -10                }            }        ]    } }</pre>  *<p>  * Background reading:  *<ul>  *<li><a href="http://research.microsoft.com/pubs/132652/MSR-TR-2010-82.pdf">  * Christopher J.C. Burges. From RankNet to LambdaRank to LambdaMART: An Overview.  * Microsoft Research Technical Report MSR-TR-2010-82.</a>  *</ul>  *<ul>  *<li><a href="https://papers.nips.cc/paper/3305-a-general-boosting-method-and-its-application-to-learning-ranking-functions-for-web-search.pdf">  * Z. Zheng, H. Zha, T. Zhang, O. Chapelle, K. Chen, and G. Sun. A General Boosting Method and its Application to Learning Ranking Functions for Web Search.  * Advances in Neural Information Processing Systems (NIPS), 2007.</a>  *</ul>  */
end_comment

begin_class
DECL|class|MultipleAdditiveTreesModel
specifier|public
class|class
name|MultipleAdditiveTreesModel
extends|extends
name|LTRScoringModel
block|{
DECL|field|fname2index
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|fname2index
decl_stmt|;
DECL|field|trees
specifier|private
name|List
argument_list|<
name|RegressionTree
argument_list|>
name|trees
decl_stmt|;
DECL|method|createRegressionTree
specifier|private
name|RegressionTree
name|createRegressionTree
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
specifier|final
name|RegressionTree
name|rt
init|=
operator|new
name|RegressionTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|rt
argument_list|,
name|map
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rt
return|;
block|}
DECL|method|createRegressionTreeNode
specifier|private
name|RegressionTreeNode
name|createRegressionTreeNode
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
specifier|final
name|RegressionTreeNode
name|rtn
init|=
operator|new
name|RegressionTreeNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|rtn
argument_list|,
name|map
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rtn
return|;
block|}
DECL|class|RegressionTreeNode
specifier|public
class|class
name|RegressionTreeNode
block|{
DECL|field|NODE_SPLIT_SLACK
specifier|private
specifier|static
specifier|final
name|float
name|NODE_SPLIT_SLACK
init|=
literal|1E
operator|-
literal|6f
decl_stmt|;
DECL|field|value
specifier|private
name|float
name|value
init|=
literal|0f
decl_stmt|;
DECL|field|feature
specifier|private
name|String
name|feature
decl_stmt|;
DECL|field|featureIndex
specifier|private
name|int
name|featureIndex
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|threshold
specifier|private
name|Float
name|threshold
decl_stmt|;
DECL|field|left
specifier|private
name|RegressionTreeNode
name|left
decl_stmt|;
DECL|field|right
specifier|private
name|RegressionTreeNode
name|right
decl_stmt|;
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setFeature
specifier|public
name|void
name|setFeature
parameter_list|(
name|String
name|feature
parameter_list|)
block|{
name|this
operator|.
name|feature
operator|=
name|feature
expr_stmt|;
specifier|final
name|Integer
name|idx
init|=
name|fname2index
operator|.
name|get
argument_list|(
name|this
operator|.
name|feature
argument_list|)
decl_stmt|;
comment|// this happens if the tree specifies a feature that does not exist
comment|// this could be due to lambdaSmart building off of pre-existing trees
comment|// that use a feature that is no longer output during feature extraction
name|featureIndex
operator|=
operator|(
name|idx
operator|==
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
name|idx
expr_stmt|;
block|}
DECL|method|setThreshold
specifier|public
name|void
name|setThreshold
parameter_list|(
name|float
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|threshold
operator|=
name|threshold
operator|+
name|NODE_SPLIT_SLACK
expr_stmt|;
block|}
DECL|method|setThreshold
specifier|public
name|void
name|setThreshold
parameter_list|(
name|String
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|threshold
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|threshold
argument_list|)
operator|+
name|NODE_SPLIT_SLACK
expr_stmt|;
block|}
DECL|method|setLeft
specifier|public
name|void
name|setLeft
parameter_list|(
name|Object
name|left
parameter_list|)
block|{
name|this
operator|.
name|left
operator|=
name|createRegressionTreeNode
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|left
argument_list|)
expr_stmt|;
block|}
DECL|method|setRight
specifier|public
name|void
name|setRight
parameter_list|(
name|Object
name|right
parameter_list|)
block|{
name|this
operator|.
name|right
operator|=
name|createRegressionTreeNode
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|right
argument_list|)
expr_stmt|;
block|}
DECL|method|isLeaf
specifier|public
name|boolean
name|isLeaf
parameter_list|()
block|{
return|return
name|feature
operator|==
literal|null
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|float
index|[]
name|featureVector
parameter_list|)
block|{
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
block|{
return|return
name|value
return|;
block|}
comment|// unsupported feature (tree is looking for a feature that does not exist)
if|if
condition|(
operator|(
name|featureIndex
operator|<
literal|0
operator|)
operator|||
operator|(
name|featureIndex
operator|>=
name|featureVector
operator|.
name|length
operator|)
condition|)
block|{
return|return
literal|0f
return|;
block|}
if|if
condition|(
name|featureVector
index|[
name|featureIndex
index|]
operator|<=
name|threshold
condition|)
block|{
return|return
name|left
operator|.
name|score
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|right
operator|.
name|score
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
block|}
DECL|method|explain
specifier|public
name|String
name|explain
parameter_list|(
name|float
index|[]
name|featureVector
parameter_list|)
block|{
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
block|{
return|return
literal|"val: "
operator|+
name|value
return|;
block|}
comment|// unsupported feature (tree is looking for a feature that does not exist)
if|if
condition|(
operator|(
name|featureIndex
operator|<
literal|0
operator|)
operator|||
operator|(
name|featureIndex
operator|>=
name|featureVector
operator|.
name|length
operator|)
condition|)
block|{
return|return
literal|"'"
operator|+
name|feature
operator|+
literal|"' does not exist in FV, Return Zero"
return|;
block|}
comment|// could store extra information about how much training data supported
comment|// each branch and report
comment|// that here
if|if
condition|(
name|featureVector
index|[
name|featureIndex
index|]
operator|<=
name|threshold
condition|)
block|{
name|String
name|rval
init|=
literal|"'"
operator|+
name|feature
operator|+
literal|"':"
operator|+
name|featureVector
index|[
name|featureIndex
index|]
operator|+
literal|"<= "
operator|+
name|threshold
operator|+
literal|", Go Left | "
decl_stmt|;
return|return
name|rval
operator|+
name|left
operator|.
name|explain
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|rval
init|=
literal|"'"
operator|+
name|feature
operator|+
literal|"':"
operator|+
name|featureVector
index|[
name|featureIndex
index|]
operator|+
literal|"> "
operator|+
name|threshold
operator|+
literal|", Go Right | "
decl_stmt|;
return|return
name|rval
operator|+
name|right
operator|.
name|explain
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"(feature="
argument_list|)
operator|.
name|append
argument_list|(
name|feature
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",threshold="
argument_list|)
operator|.
name|append
argument_list|(
name|threshold
operator|.
name|floatValue
argument_list|()
operator|-
name|NODE_SPLIT_SLACK
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",left="
argument_list|)
operator|.
name|append
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",right="
argument_list|)
operator|.
name|append
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|RegressionTreeNode
specifier|public
name|RegressionTreeNode
parameter_list|()
block|{     }
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|ModelException
block|{
if|if
condition|(
name|isLeaf
argument_list|()
condition|)
block|{
if|if
condition|(
name|left
operator|!=
literal|null
operator|||
name|right
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree node is leaf with left="
operator|+
name|left
operator|+
literal|" and right="
operator|+
name|right
argument_list|)
throw|;
block|}
return|return;
block|}
if|if
condition|(
literal|null
operator|==
name|threshold
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree node is missing threshold"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|left
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree node is missing left"
argument_list|)
throw|;
block|}
else|else
block|{
name|left
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|right
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree node is missing right"
argument_list|)
throw|;
block|}
else|else
block|{
name|right
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|RegressionTree
specifier|public
class|class
name|RegressionTree
block|{
DECL|field|weight
specifier|private
name|Float
name|weight
decl_stmt|;
DECL|field|root
specifier|private
name|RegressionTreeNode
name|root
decl_stmt|;
DECL|method|setWeight
specifier|public
name|void
name|setWeight
parameter_list|(
name|float
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
operator|new
name|Float
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
DECL|method|setWeight
specifier|public
name|void
name|setWeight
parameter_list|(
name|String
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
operator|new
name|Float
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
DECL|method|setRoot
specifier|public
name|void
name|setRoot
parameter_list|(
name|Object
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|createRegressionTreeNode
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|root
argument_list|)
expr_stmt|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|float
index|[]
name|featureVector
parameter_list|)
block|{
return|return
name|weight
operator|.
name|floatValue
argument_list|()
operator|*
name|root
operator|.
name|score
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|String
name|explain
parameter_list|(
name|float
index|[]
name|featureVector
parameter_list|)
block|{
return|return
name|root
operator|.
name|explain
argument_list|(
name|featureVector
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"(weight="
argument_list|)
operator|.
name|append
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",root="
argument_list|)
operator|.
name|append
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|RegressionTree
specifier|public
name|RegressionTree
parameter_list|()
block|{     }
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|ModelException
block|{
if|if
condition|(
name|weight
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree doesn't contain a weight"
argument_list|)
throw|;
block|}
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"MultipleAdditiveTreesModel tree doesn't contain a tree"
argument_list|)
throw|;
block|}
else|else
block|{
name|root
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|setTrees
specifier|public
name|void
name|setTrees
parameter_list|(
name|Object
name|trees
parameter_list|)
block|{
name|this
operator|.
name|trees
operator|=
operator|new
name|ArrayList
argument_list|<
name|RegressionTree
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Object
name|o
range|:
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|trees
control|)
block|{
specifier|final
name|RegressionTree
name|rt
init|=
name|createRegressionTree
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
argument_list|)
decl_stmt|;
name|this
operator|.
name|trees
operator|.
name|add
argument_list|(
name|rt
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|MultipleAdditiveTreesModel
specifier|public
name|MultipleAdditiveTreesModel
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Feature
argument_list|>
name|features
parameter_list|,
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
parameter_list|,
name|String
name|featureStoreName
parameter_list|,
name|List
argument_list|<
name|Feature
argument_list|>
name|allFeatures
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
name|featureStoreName
argument_list|,
name|allFeatures
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|fname2index
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|features
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|String
name|key
init|=
name|features
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|fname2index
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|validate
specifier|protected
name|void
name|validate
parameter_list|()
throws|throws
name|ModelException
block|{
name|super
operator|.
name|validate
argument_list|()
expr_stmt|;
if|if
condition|(
name|trees
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ModelException
argument_list|(
literal|"no trees declared for model "
operator|+
name|name
argument_list|)
throw|;
block|}
for|for
control|(
name|RegressionTree
name|tree
range|:
name|trees
control|)
block|{
name|tree
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|float
index|[]
name|modelFeatureValuesNormalized
parameter_list|)
block|{
name|float
name|score
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|RegressionTree
name|t
range|:
name|trees
control|)
block|{
name|score
operator|+=
name|t
operator|.
name|score
argument_list|(
name|modelFeatureValuesNormalized
argument_list|)
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
comment|// /////////////////////////////////////////
comment|// produces a string that looks like:
comment|// 40.0 = multipleadditivetreesmodel [ org.apache.solr.ltr.model.MultipleAdditiveTreesModel ]
comment|// model applied to
comment|// features, sum of:
comment|// 50.0 = tree 0 | 'matchedTitle':1.0> 0.500001, Go Right |
comment|// 'this_feature_doesnt_exist' does not
comment|// exist in FV, Go Left | val: 50.0
comment|// -10.0 = tree 1 | val: -10.0
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|finalScore
parameter_list|,
name|List
argument_list|<
name|Explanation
argument_list|>
name|featureExplanations
parameter_list|)
block|{
specifier|final
name|float
index|[]
name|fv
init|=
operator|new
name|float
index|[
name|featureExplanations
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Explanation
name|featureExplain
range|:
name|featureExplanations
control|)
block|{
name|fv
index|[
name|index
index|]
operator|=
name|featureExplain
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Explanation
argument_list|>
name|details
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
for|for
control|(
specifier|final
name|RegressionTree
name|t
range|:
name|trees
control|)
block|{
specifier|final
name|float
name|score
init|=
name|t
operator|.
name|score
argument_list|(
name|fv
argument_list|)
decl_stmt|;
specifier|final
name|Explanation
name|p
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|,
literal|"tree "
operator|+
name|index
operator|+
literal|" | "
operator|+
name|t
operator|.
name|explain
argument_list|(
name|fv
argument_list|)
argument_list|)
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|finalScore
argument_list|,
name|toString
argument_list|()
operator|+
literal|" model applied to features, sum of:"
argument_list|,
name|details
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"(name="
argument_list|)
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",trees=["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|trees
operator|.
name|size
argument_list|()
condition|;
operator|++
name|ii
control|)
block|{
if|if
condition|(
name|ii
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|trees
operator|.
name|get
argument_list|(
name|ii
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"])"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
