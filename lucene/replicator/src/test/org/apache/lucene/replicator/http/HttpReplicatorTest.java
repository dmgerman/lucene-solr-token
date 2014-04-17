begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator.http
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|http
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|BasicClientConnectionManager
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
name|document
operator|.
name|Document
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|SnapshotDeletionPolicy
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
name|replicator
operator|.
name|IndexReplicationHandler
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
name|replicator
operator|.
name|IndexRevision
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
name|replicator
operator|.
name|LocalReplicator
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
name|replicator
operator|.
name|PerSessionDirectoryFactory
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
name|replicator
operator|.
name|ReplicationClient
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
name|replicator
operator|.
name|Replicator
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
name|replicator
operator|.
name|ReplicatorTestCase
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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

begin_class
DECL|class|HttpReplicatorTest
specifier|public
class|class
name|HttpReplicatorTest
extends|extends
name|ReplicatorTestCase
block|{
DECL|field|clientWorkDir
specifier|private
name|File
name|clientWorkDir
decl_stmt|;
DECL|field|serverReplicator
specifier|private
name|Replicator
name|serverReplicator
decl_stmt|;
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|reader
specifier|private
name|DirectoryReader
name|reader
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
decl_stmt|;
DECL|field|serverIndexDir
DECL|field|handlerIndexDir
specifier|private
name|Directory
name|serverIndexDir
decl_stmt|,
name|handlerIndexDir
decl_stmt|;
DECL|field|replicationServlet
specifier|private
name|ReplicationServlet
name|replicationServlet
decl_stmt|;
DECL|method|startServer
specifier|private
name|void
name|startServer
parameter_list|()
throws|throws
name|Exception
block|{
name|ServletHandler
name|replicationHandler
init|=
operator|new
name|ServletHandler
argument_list|()
decl_stmt|;
name|ReplicationService
name|service
init|=
operator|new
name|ReplicationService
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"s1"
argument_list|,
name|serverReplicator
argument_list|)
argument_list|)
decl_stmt|;
name|replicationServlet
operator|=
operator|new
name|ReplicationServlet
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|ServletHolder
name|servlet
init|=
operator|new
name|ServletHolder
argument_list|(
name|replicationServlet
argument_list|)
decl_stmt|;
name|replicationHandler
operator|.
name|addServletWithMapping
argument_list|(
name|servlet
argument_list|,
name|ReplicationService
operator|.
name|REPLICATION_CONTEXT
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
name|server
operator|=
name|newHttpServer
argument_list|(
name|replicationHandler
argument_list|)
expr_stmt|;
name|port
operator|=
name|serverPort
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|host
operator|=
name|serverHost
argument_list|(
name|server
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.eclipse.jetty.LEVEL"
argument_list|,
literal|"DEBUG"
argument_list|)
expr_stmt|;
comment|// sets stderr logging to DEBUG level
name|clientWorkDir
operator|=
name|createTempDir
argument_list|(
literal|"httpReplicatorTest"
argument_list|)
expr_stmt|;
name|handlerIndexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|serverIndexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|serverReplicator
operator|=
operator|new
name|LocalReplicator
argument_list|()
expr_stmt|;
name|startServer
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|serverIndexDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
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
name|stopHttpServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|handlerIndexDir
argument_list|,
name|serverIndexDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"org.eclipse.jetty.LEVEL"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|publishRevision
specifier|private
name|void
name|publishRevision
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setCommitData
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"ID"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|serverReplicator
operator|.
name|publish
argument_list|(
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|reopenReader
specifier|private
name|void
name|reopenReader
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|newReader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newReader
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Replicator
name|replicator
init|=
operator|new
name|HttpReplicator
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|ReplicationService
operator|.
name|REPLICATION_CONTEXT
operator|+
literal|"/s1"
argument_list|,
name|getClientConnectionManager
argument_list|()
argument_list|)
decl_stmt|;
name|ReplicationClient
name|client
init|=
operator|new
name|ReplicationClient
argument_list|(
name|replicator
argument_list|,
operator|new
name|IndexReplicationHandler
argument_list|(
name|handlerIndexDir
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|PerSessionDirectoryFactory
argument_list|(
name|clientWorkDir
argument_list|)
argument_list|)
decl_stmt|;
name|publishRevision
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|reopenReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|publishRevision
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|reopenReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testServerErrors
specifier|public
name|void
name|testServerErrors
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests the behaviour of the client when the server sends an error
comment|// must use BasicClientConnectionManager to test whether the client is closed correctly
name|BasicClientConnectionManager
name|conMgr
init|=
operator|new
name|BasicClientConnectionManager
argument_list|()
decl_stmt|;
name|Replicator
name|replicator
init|=
operator|new
name|HttpReplicator
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|ReplicationService
operator|.
name|REPLICATION_CONTEXT
operator|+
literal|"/s1"
argument_list|,
name|conMgr
argument_list|)
decl_stmt|;
name|ReplicationClient
name|client
init|=
operator|new
name|ReplicationClient
argument_list|(
name|replicator
argument_list|,
operator|new
name|IndexReplicationHandler
argument_list|(
name|handlerIndexDir
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|PerSessionDirectoryFactory
argument_list|(
name|clientWorkDir
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|publishRevision
argument_list|(
literal|5
argument_list|)
expr_stmt|;
try|try
block|{
name|replicationServlet
operator|.
name|setRespondWithError
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// expected
block|}
name|replicationServlet
operator|.
name|setRespondWithError
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|client
operator|.
name|updateNow
argument_list|()
expr_stmt|;
comment|// now it should work
name|reopenReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|replicationServlet
operator|.
name|setRespondWithError
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

