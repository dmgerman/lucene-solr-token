begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|common
operator|.
name|cloud
operator|.
name|SecurityAwareZkACLProvider
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
name|SolrZkClient
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
name|VMParamsAllAndReadonlyDigestZkACLProvider
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
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
operator|.
name|NoAuthException
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|VMParamsZkACLAndCredentialsProvidersTest
specifier|public
class|class
name|VMParamsZkACLAndCredentialsProvidersTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DATA_ENCODING
specifier|private
specifier|static
specifier|final
name|Charset
name|DATA_ENCODING
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|createTempDir
argument_list|()
expr_stmt|;
name|zkDir
operator|=
name|createTempDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"ZooKeeper dataDir:"
operator|+
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|setSecuritySystemProperties
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/protectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/protectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
name|SecurityAwareZkACLProvider
operator|.
name|SECURITY_ZNODE_PATH
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
comment|// Currently no credentials on ZK connection, because those same VM-params are used for adding ACLs, and here we want
comment|// no (or completely open) ACLs added. Therefore hack your way into being authorized for creating anyway
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|addAuthInfo
argument_list|(
literal|"digest"
argument_list|,
operator|(
literal|"connectAndAllACLUsername:connectAndAllACLPassword"
operator|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/unprotectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/unprotectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoCredentials
specifier|public
name|void
name|testNoCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|useNoCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWrongCredentials
specifier|public
name|void
name|testWrongCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|useWrongCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllCredentials
specifier|public
name|void
name|testAllCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|useAllCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadonlyCredentials
specifier|public
name|void
name|testReadonlyCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|useReadonlyCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTest
specifier|protected
specifier|static
name|void
name|doTest
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|boolean
name|getData
parameter_list|,
name|boolean
name|list
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|setData
parameter_list|,
name|boolean
name|delete
parameter_list|,
name|boolean
name|secureGet
parameter_list|,
name|boolean
name|secureList
parameter_list|,
name|boolean
name|secureCreate
parameter_list|,
name|boolean
name|secureSet
parameter_list|,
name|boolean
name|secureDelete
parameter_list|)
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|"/protectedCreateNode"
argument_list|,
name|getData
argument_list|,
name|list
argument_list|,
name|create
argument_list|,
name|setData
argument_list|,
name|delete
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|"/protectedMakePathNode"
argument_list|,
name|getData
argument_list|,
name|list
argument_list|,
name|create
argument_list|,
name|setData
argument_list|,
name|delete
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|"/unprotectedCreateNode"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|delete
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|"/unprotectedMakePathNode"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|delete
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
name|zkClient
argument_list|,
name|SecurityAwareZkACLProvider
operator|.
name|SECURITY_ZNODE_PATH
argument_list|,
name|secureGet
argument_list|,
name|secureList
argument_list|,
name|secureCreate
argument_list|,
name|secureSet
argument_list|,
name|secureDelete
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest
specifier|protected
specifier|static
name|void
name|doTest
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|getData
parameter_list|,
name|boolean
name|list
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|setData
parameter_list|,
name|boolean
name|delete
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getData
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|getData
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
try|try
block|{
name|zkClient
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|list
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|list
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
try|try
block|{
name|zkClient
operator|.
name|create
argument_list|(
name|path
operator|+
literal|"/subnode"
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|create
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
else|else
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|path
operator|+
literal|"/subnode"
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|create
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|path
operator|+
literal|"/subnode/subsubnode"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|create
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
else|else
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|path
operator|+
literal|"/subnode/subsubnode"
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|delete
argument_list|(
name|path
operator|+
literal|"/subnode"
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|create
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
try|try
block|{
name|zkClient
operator|.
name|setData
argument_list|(
name|path
argument_list|,
operator|(
name|byte
index|[]
operator|)
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|setData
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|setData
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
try|try
block|{
comment|// Actually about the ACLs on /solr, but that is protected
name|zkClient
operator|.
name|delete
argument_list|(
name|path
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|delete
condition|)
name|fail
argument_list|(
literal|"NoAuthException expected "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoAuthException
name|nae
parameter_list|)
block|{
if|if
condition|(
name|delete
condition|)
name|fail
argument_list|(
literal|"No NoAuthException expected"
argument_list|)
expr_stmt|;
comment|// expected
block|}
block|}
DECL|method|useNoCredentials
specifier|private
name|void
name|useNoCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
block|}
DECL|method|useWrongCredentials
specifier|private
name|void
name|useWrongCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_ACL_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|,
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPasswordWrong"
argument_list|)
expr_stmt|;
block|}
DECL|method|useAllCredentials
specifier|private
name|void
name|useAllCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_CRED_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|,
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|useReadonlyCredentials
specifier|private
name|void
name|useReadonlyCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_CRED_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|,
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|setSecuritySystemProperties
specifier|private
name|void
name|setSecuritySystemProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_ACL_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|,
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_CRED_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|,
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|clearSecuritySystemProperties
specifier|private
name|void
name|clearSecuritySystemProperties
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_ACL_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|SolrZkClient
operator|.
name|ZK_CRED_PROVIDER_CLASS_NAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_USERNAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_PASSWORD_VM_PARAM_NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

