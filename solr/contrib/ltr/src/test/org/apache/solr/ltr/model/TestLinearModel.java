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
name|Collections
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|TestRerankBase
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
name|IdentityNormalizer
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
name|ltr
operator|.
name|store
operator|.
name|FeatureStore
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
name|store
operator|.
name|rest
operator|.
name|ManagedModelStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestLinearModel
specifier|public
class|class
name|TestLinearModel
extends|extends
name|TestRerankBase
block|{
DECL|method|createLinearModel
specifier|public
specifier|static
name|LTRScoringModel
name|createLinearModel
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
throws|throws
name|ModelException
block|{
specifier|final
name|LTRScoringModel
name|model
init|=
name|LTRScoringModel
operator|.
name|getInstance
argument_list|(
name|solrResourceLoader
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
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
decl_stmt|;
return|return
name|model
return|;
block|}
DECL|field|store
specifier|static
name|ManagedModelStore
name|store
init|=
literal|null
decl_stmt|;
DECL|field|fstore
specifier|static
name|FeatureStore
name|fstore
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|setuptest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// loadFeatures("features-store-test-model.json");
name|store
operator|=
name|getManagedModelStore
argument_list|()
expr_stmt|;
name|fstore
operator|=
name|getManagedFeatureStore
argument_list|()
operator|.
name|getFeatureStore
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getInstanceTest
specifier|public
name|void
name|getInstanceTest
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant1"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant5"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant5"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"weights"
argument_list|,
name|weights
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|ltrScoringModel
init|=
name|createLinearModel
argument_list|(
literal|"test1"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|store
operator|.
name|addModel
argument_list|(
name|ltrScoringModel
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|m
init|=
name|store
operator|.
name|getModel
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ltrScoringModel
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nullFeatureWeightsTest
specifier|public
name|void
name|nullFeatureWeightsTest
parameter_list|()
block|{
specifier|final
name|ModelException
name|expectedException
init|=
operator|new
name|ModelException
argument_list|(
literal|"Model test2 doesn't contain any weights"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant5"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|createLinearModel
argument_list|(
literal|"test2"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unexpectedly got here instead of catching "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ModelException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|existingNameTest
specifier|public
name|void
name|existingNameTest
parameter_list|()
block|{
specifier|final
name|SolrException
name|expectedException
init|=
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ModelException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|": model 'test3' already exists. Please use a different name"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant5"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant1"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant5"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"weights"
argument_list|,
name|weights
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|ltrScoringModel
init|=
name|createLinearModel
argument_list|(
literal|"test3"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|store
operator|.
name|addModel
argument_list|(
name|ltrScoringModel
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|m
init|=
name|store
operator|.
name|getModel
argument_list|(
literal|"test3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ltrScoringModel
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|store
operator|.
name|addModel
argument_list|(
name|ltrScoringModel
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unexpectedly got here instead of catching "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|duplicateFeatureTest
specifier|public
name|void
name|duplicateFeatureTest
parameter_list|()
block|{
specifier|final
name|ModelException
name|expectedException
init|=
operator|new
name|ModelException
argument_list|(
literal|"duplicated feature constant1 in model test4"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant1"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant1"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant5"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"weights"
argument_list|,
name|weights
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|ltrScoringModel
init|=
name|createLinearModel
argument_list|(
literal|"test4"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|store
operator|.
name|addModel
argument_list|(
name|ltrScoringModel
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unexpectedly got here instead of catching "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ModelException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|missingFeatureWeightTest
specifier|public
name|void
name|missingFeatureWeightTest
parameter_list|()
block|{
specifier|final
name|ModelException
name|expectedException
init|=
operator|new
name|ModelException
argument_list|(
literal|"Model test5 lacks weight(s) for [constant5]"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant5"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant1"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant5missing"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"weights"
argument_list|,
name|weights
argument_list|)
expr_stmt|;
name|createLinearModel
argument_list|(
literal|"test5"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unexpectedly got here instead of catching "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ModelException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|emptyFeaturesTest
specifier|public
name|void
name|emptyFeaturesTest
parameter_list|()
block|{
specifier|final
name|ModelException
name|expectedException
init|=
operator|new
name|ModelException
argument_list|(
literal|"no features declared for model test6"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Feature
argument_list|>
name|features
init|=
name|getFeatures
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Normalizer
argument_list|>
name|norms
init|=
operator|new
name|ArrayList
argument_list|<
name|Normalizer
argument_list|>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|features
operator|.
name|size
argument_list|()
argument_list|,
name|IdentityNormalizer
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|weights
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant1"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|weights
operator|.
name|put
argument_list|(
literal|"constant5missing"
argument_list|,
literal|1d
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"weights"
argument_list|,
name|weights
argument_list|)
expr_stmt|;
specifier|final
name|LTRScoringModel
name|ltrScoringModel
init|=
name|createLinearModel
argument_list|(
literal|"test6"
argument_list|,
name|features
argument_list|,
name|norms
argument_list|,
literal|"test"
argument_list|,
name|fstore
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|store
operator|.
name|addModel
argument_list|(
name|ltrScoringModel
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unexpectedly got here instead of catching "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ModelException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

