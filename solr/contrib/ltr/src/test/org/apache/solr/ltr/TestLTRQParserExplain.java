begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|model
operator|.
name|LinearModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
DECL|class|TestLTRQParserExplain
specifier|public
class|class
name|TestLTRQParserExplain
extends|extends
name|TestRerankBase
block|{
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
name|loadFeatures
argument_list|(
literal|"features-store-test-model.json"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after
specifier|public
specifier|static
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|aftertest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRerankedExplain
specifier|public
name|void
name|testRerankedExplain
parameter_list|()
throws|throws
name|Exception
block|{
name|loadModel
argument_list|(
literal|"linear2"
argument_list|,
name|LinearModel
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"constant1"
block|,
literal|"constant2"
block|,
literal|"pop"
block|}
argument_list|,
literal|"{\"weights\":{\"pop\":1.0,\"constant1\":1.5,\"constant2\":3.5}}"
argument_list|)
expr_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=2 model=linear2}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/9=='\n13.5 = LinearModel(name=linear2,featureWeights=[constant1=1.5,constant2=3.5,pop=1.0]) model applied to features, sum of:\n  1.5 = prod of:\n    1.5 = weight on feature\n    1.0 = ValueFeature [name=constant1, params={value=1}]\n  7.0 = prod of:\n    3.5 = weight on feature\n    2.0 = ValueFeature [name=constant2, params={value=2}]\n  5.0 = prod of:\n    1.0 = weight on feature\n    5.0 = FieldValueFeature [name=pop, params={field=popularity}]\n'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRerankedExplainSameBetweenDifferentDocsWithSameFeatures
specifier|public
name|void
name|testRerankedExplainSameBetweenDifferentDocsWithSameFeatures
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeatures
argument_list|(
literal|"features-linear.json"
argument_list|)
expr_stmt|;
name|loadModels
argument_list|(
literal|"linear-model.json"
argument_list|)
expr_stmt|;
specifier|final
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=4 model=6029760550880411648}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedExplainNormalizer
init|=
literal|"normalized using MinMaxNormalizer(min=0.0,max=10.0)"
decl_stmt|;
specifier|final
name|String
name|expectedExplain
init|=
literal|"\n3.5116758 = LinearModel(name=6029760550880411648,featureWeights=["
operator|+
literal|"title=0.0,"
operator|+
literal|"description=0.1,"
operator|+
literal|"keywords=0.2,"
operator|+
literal|"popularity=0.3,"
operator|+
literal|"text=0.4,"
operator|+
literal|"queryIntentPerson=0.1231231,"
operator|+
literal|"queryIntentCompany=0.12121211"
operator|+
literal|"]) model applied to features, sum of:\n  0.0 = prod of:\n    0.0 = weight on feature\n    1.0 = ValueFeature [name=title, params={value=1}]\n  0.2 = prod of:\n    0.1 = weight on feature\n    2.0 = ValueFeature [name=description, params={value=2}]\n  0.4 = prod of:\n    0.2 = weight on feature\n    2.0 = ValueFeature [name=keywords, params={value=2}]\n  0.09 = prod of:\n    0.3 = weight on feature\n    0.3 = "
operator|+
name|expectedExplainNormalizer
operator|+
literal|"\n      3.0 = ValueFeature [name=popularity, params={value=3}]\n  1.6 = prod of:\n    0.4 = weight on feature\n    4.0 = ValueFeature [name=text, params={value=4}]\n  0.6156155 = prod of:\n    0.1231231 = weight on feature\n    5.0 = ValueFeature [name=queryIntentPerson, params={value=5}]\n  0.60606056 = prod of:\n    0.12121211 = weight on feature\n    5.0 = ValueFeature [name=queryIntentCompany, params={value=5}]\n"
decl_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/7=='"
operator|+
name|expectedExplain
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/9=='"
operator|+
name|expectedExplain
operator|+
literal|"'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|LinearScoreExplainMissingEfiFeatureShouldReturnDefaultScore
specifier|public
name|void
name|LinearScoreExplainMissingEfiFeatureShouldReturnDefaultScore
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeatures
argument_list|(
literal|"features-linear-efi.json"
argument_list|)
expr_stmt|;
name|loadModels
argument_list|(
literal|"linear-model-efi.json"
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=4 model=linear-efi}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|linearModelEfiString
init|=
literal|"LinearModel(name=linear-efi,featureWeights=["
operator|+
literal|"sampleConstant=1.0,"
operator|+
literal|"search_number_of_nights=2.0])"
decl_stmt|;
name|query
operator|.
name|remove
argument_list|(
literal|"wt"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/7=='\n5.0 = "
operator|+
name|linearModelEfiString
operator|+
literal|" model applied to features, sum of:\n  5.0 = prod of:\n    1.0 = weight on feature\n    5.0 = ValueFeature [name=sampleConstant, params={value=5}]\n"
operator|+
literal|"  0.0 = prod of:\n"
operator|+
literal|"    2.0 = weight on feature\n"
operator|+
literal|"    0.0 = The feature has no value\n'}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/9=='\n5.0 = "
operator|+
name|linearModelEfiString
operator|+
literal|" model applied to features, sum of:\n  5.0 = prod of:\n    1.0 = weight on feature\n    5.0 = ValueFeature [name=sampleConstant, params={value=5}]\n"
operator|+
literal|"  0.0 = prod of:\n"
operator|+
literal|"    2.0 = weight on feature\n"
operator|+
literal|"    0.0 = The feature has no value\n'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multipleAdditiveTreesScoreExplainMissingEfiFeatureShouldReturnDefaultScore
specifier|public
name|void
name|multipleAdditiveTreesScoreExplainMissingEfiFeatureShouldReturnDefaultScore
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeatures
argument_list|(
literal|"external_features_for_sparse_processing.json"
argument_list|)
expr_stmt|;
name|loadModels
argument_list|(
literal|"multipleadditivetreesmodel_external_binary_features.json"
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"title:bloomberg"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"rq"
argument_list|,
literal|"{!ltr reRankDocs=4 model=external_model_binary_feature efi.user_device_tablet=1}"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|tree1
init|=
literal|"(weight=1.0,root=(feature=user_device_smartphone,threshold=0.5,left=0.0,right=50.0))"
decl_stmt|;
specifier|final
name|String
name|tree2
init|=
literal|"(weight=1.0,root=(feature=user_device_tablet,threshold=0.5,left=0.0,right=65.0))"
decl_stmt|;
specifier|final
name|String
name|trees
init|=
literal|"["
operator|+
name|tree1
operator|+
literal|","
operator|+
name|tree2
operator|+
literal|"]"
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/7=='\n"
operator|+
literal|"65.0 = MultipleAdditiveTreesModel(name=external_model_binary_feature,trees="
operator|+
name|trees
operator|+
literal|") model applied to features, sum of:\n"
operator|+
literal|"  0.0 = tree 0 | \\'user_device_smartphone\\':0.0<= 0.500001, Go Left | val: 0.0\n"
operator|+
literal|"  65.0 = tree 1 | \\'user_device_tablet\\':1.0> 0.500001, Go Right | val: 65.0\n'}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/query"
operator|+
name|query
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|"/debug/explain/9=='\n"
operator|+
literal|"65.0 = MultipleAdditiveTreesModel(name=external_model_binary_feature,trees="
operator|+
name|trees
operator|+
literal|") model applied to features, sum of:\n"
operator|+
literal|"  0.0 = tree 0 | \\'user_device_smartphone\\':0.0<= 0.500001, Go Left | val: 0.0\n"
operator|+
literal|"  65.0 = tree 1 | \\'user_device_tablet\\':1.0> 0.500001, Go Right | val: 65.0\n'}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

