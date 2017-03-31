begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|RecoveryStrategy
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
name|cloud
operator|.
name|ZkCoreNodeProps
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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

begin_comment
comment|/**  * test that configs can override the RecoveryStrategy  */
end_comment

begin_class
DECL|class|ConfigureRecoveryStrategyTest
specifier|public
class|class
name|ConfigureRecoveryStrategyTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrConfigFileNameConfigure
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameConfigure
init|=
literal|"solrconfig-configurerecoverystrategy.xml"
decl_stmt|;
DECL|field|solrConfigFileNameCustom
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameCustom
init|=
literal|"solrconfig-customrecoverystrategy.xml"
decl_stmt|;
DECL|field|solrConfigFileName
specifier|private
specifier|static
name|String
name|solrConfigFileName
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|solrConfigFileName
operator|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameConfigure
else|:
name|solrConfigFileNameCustom
operator|)
expr_stmt|;
name|initCore
argument_list|(
name|solrConfigFileName
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuilder
specifier|public
name|void
name|testBuilder
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RecoveryStrategy
operator|.
name|Builder
name|recoveryStrategyBuilder
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getRecoveryStrategyBuilder
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"recoveryStrategyBuilder is null"
argument_list|,
name|recoveryStrategyBuilder
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedClassName
decl_stmt|;
if|if
condition|(
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameConfigure
argument_list|)
condition|)
block|{
name|expectedClassName
operator|=
name|RecoveryStrategy
operator|.
name|Builder
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameCustom
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"recoveryStrategyBuilder is wrong class (instanceof)"
argument_list|,
name|recoveryStrategyBuilder
operator|instanceof
name|CustomRecoveryStrategyBuilder
argument_list|)
expr_stmt|;
name|expectedClassName
operator|=
name|ConfigureRecoveryStrategyTest
operator|.
name|CustomRecoveryStrategyBuilder
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expectedClassName
operator|=
literal|null
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"recoveryStrategyBuilder is wrong class (name)"
argument_list|,
name|expectedClassName
argument_list|,
name|recoveryStrategyBuilder
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlmostAllMethodsAreFinal
specifier|public
name|void
name|testAlmostAllMethodsAreFinal
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Method
name|m
range|:
name|RecoveryStrategy
operator|.
name|class
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
continue|continue;
specifier|final
name|String
name|methodName
init|=
name|m
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"getReplicateLeaderUrl"
operator|.
name|equals
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|,
name|Modifier
operator|.
name|isFinal
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|,
name|Modifier
operator|.
name|isFinal
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CustomRecoveryStrategy
specifier|static
specifier|public
class|class
name|CustomRecoveryStrategy
extends|extends
name|RecoveryStrategy
block|{
DECL|field|alternativeBaseUrlProp
specifier|private
name|String
name|alternativeBaseUrlProp
decl_stmt|;
DECL|method|getAlternativeBaseUrlProp
specifier|public
name|String
name|getAlternativeBaseUrlProp
parameter_list|()
block|{
return|return
name|alternativeBaseUrlProp
return|;
block|}
DECL|method|setAlternativeBaseUrlProp
specifier|public
name|void
name|setAlternativeBaseUrlProp
parameter_list|(
name|String
name|alternativeBaseUrlProp
parameter_list|)
block|{
name|this
operator|.
name|alternativeBaseUrlProp
operator|=
name|alternativeBaseUrlProp
expr_stmt|;
block|}
DECL|method|CustomRecoveryStrategy
specifier|public
name|CustomRecoveryStrategy
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|,
name|RecoveryStrategy
operator|.
name|RecoveryListener
name|recoveryListener
parameter_list|)
block|{
name|super
argument_list|(
name|cc
argument_list|,
name|cd
argument_list|,
name|recoveryListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReplicateLeaderUrl
specifier|protected
name|String
name|getReplicateLeaderUrl
parameter_list|(
name|ZkNodeProps
name|leaderprops
parameter_list|)
block|{
return|return
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderprops
operator|.
name|getStr
argument_list|(
name|alternativeBaseUrlProp
argument_list|)
argument_list|,
name|leaderprops
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|CustomRecoveryStrategyBuilder
specifier|static
specifier|public
class|class
name|CustomRecoveryStrategyBuilder
extends|extends
name|RecoveryStrategy
operator|.
name|Builder
block|{
annotation|@
name|Override
DECL|method|newRecoveryStrategy
specifier|protected
name|RecoveryStrategy
name|newRecoveryStrategy
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|,
name|RecoveryStrategy
operator|.
name|RecoveryListener
name|recoveryListener
parameter_list|)
block|{
return|return
operator|new
name|CustomRecoveryStrategy
argument_list|(
name|cc
argument_list|,
name|cd
argument_list|,
name|recoveryListener
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
