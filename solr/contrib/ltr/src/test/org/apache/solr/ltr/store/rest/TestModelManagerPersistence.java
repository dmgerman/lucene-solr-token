begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.store.rest
package|package
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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|ValueFeature
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
name|Before
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

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_class
DECL|class|TestModelManagerPersistence
specifier|public
class|class
name|TestModelManagerPersistence
extends|extends
name|TestRerankBase
block|{
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPersistenttest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// executed first
annotation|@
name|Test
DECL|method|testFeaturePersistence
specifier|public
name|void
name|testFeaturePersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|loadFeature
argument_list|(
literal|"feature"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|"{\"value\":2}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[0]/name=='feature'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[0]/name=='feature'"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"feature1"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"value\":2}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"feature2"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|"{\"value\":2}"
argument_list|)
expr_stmt|;
name|loadFeature
argument_list|(
literal|"feature3"
argument_list|,
name|ValueFeature
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"test2"
argument_list|,
literal|"{\"value\":2}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[0]/name=='feature'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[1]/name=='feature2'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test1"
argument_list|,
literal|"/features/[0]/name=='feature1'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test2"
argument_list|,
literal|"/features/[0]/name=='feature3'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[0]/name=='feature'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test"
argument_list|,
literal|"/features/[1]/name=='feature2'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test1"
argument_list|,
literal|"/features/[0]/name=='feature1'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test2"
argument_list|,
literal|"/features/[0]/name=='feature3'"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"test-model"
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
literal|"feature"
block|}
argument_list|,
literal|"test"
argument_list|,
literal|"{\"weights\":{\"feature\":1.0}}"
argument_list|)
expr_stmt|;
name|loadModel
argument_list|(
literal|"test-model2"
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
literal|"feature1"
block|}
argument_list|,
literal|"test1"
argument_list|,
literal|"{\"weights\":{\"feature1\":1.0}}"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fstorecontent
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|fstorefile
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mstorecontent
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|mstorefile
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//check feature/model stores on deletion
specifier|final
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|fStore
init|=
call|(
name|ArrayList
argument_list|<
name|Object
argument_list|>
call|)
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|fstorecontent
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"managedList"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|fStore
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|String
name|store
init|=
call|(
name|String
call|)
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fStore
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|equals
argument_list|(
literal|"test"
argument_list|)
operator|||
name|store
operator|.
name|equals
argument_list|(
literal|"test2"
argument_list|)
operator|||
name|store
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|mStore
init|=
call|(
name|ArrayList
argument_list|<
name|Object
argument_list|>
call|)
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|mstorecontent
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"managedList"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|mStore
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|String
name|store
init|=
call|(
name|String
call|)
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mStore
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|equals
argument_list|(
literal|"test"
argument_list|)
operator|||
name|store
operator|.
name|equals
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertJDelete
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test2"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJDelete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test-model2"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test2"
argument_list|,
literal|"/features/==[]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test-model2"
argument_list|,
literal|"/models/[0]/name=='test-model'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test2"
argument_list|,
literal|"/features/==[]"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test-model2"
argument_list|,
literal|"/models/[0]/name=='test-model'"
argument_list|)
expr_stmt|;
name|assertJDelete
argument_list|(
name|ManagedModelStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test-model1"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJDelete
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test1"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test1"
argument_list|,
literal|"/features/==[]"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|ManagedFeatureStore
operator|.
name|REST_END_POINT
operator|+
literal|"/test1"
argument_list|,
literal|"/features/==[]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

