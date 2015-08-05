begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

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
name|commons
operator|.
name|exec
operator|.
name|DefaultExecutor
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
name|exec
operator|.
name|ExecuteResultHandler
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
name|LuceneTestCase
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
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|JettyConfig
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
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|JettySolrRunner
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|MiniSolrCloudCluster
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
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests the SolrCLI.RunExampleTool implementation that supports bin/solr -e [example]  */
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestSolrCLIRunExample
specifier|public
class|class
name|TestSolrCLIRunExample
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
specifier|transient
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSolrCLIRunExample
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Overrides the call to exec bin/solr to start Solr nodes to start them using the Solr test-framework    * instead of the script, since the script depends on a full build.    */
DECL|class|RunExampleExecutor
specifier|private
class|class
name|RunExampleExecutor
extends|extends
name|DefaultExecutor
implements|implements
name|Closeable
block|{
DECL|field|stdout
specifier|private
name|PrintStream
name|stdout
decl_stmt|;
DECL|field|commandsExecuted
specifier|private
name|List
argument_list|<
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
argument_list|>
name|commandsExecuted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|solrCloudCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCloudCluster
decl_stmt|;
DECL|field|standaloneSolr
specifier|private
name|JettySolrRunner
name|standaloneSolr
decl_stmt|;
DECL|method|RunExampleExecutor
name|RunExampleExecutor
parameter_list|(
name|PrintStream
name|stdout
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|stdout
operator|=
name|stdout
expr_stmt|;
block|}
comment|/**      * Override the call to execute a command asynchronously to occur synchronously during a unit test.      */
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
name|cmd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|ExecuteResultHandler
name|erh
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|code
init|=
name|execute
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to execute cmd: "
operator|+
name|joinArgs
argument_list|(
name|cmd
operator|.
name|getArguments
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|int
name|execute
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// collect the commands as they are executed for analysis by the test
name|commandsExecuted
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|String
name|exe
init|=
name|cmd
operator|.
name|getExecutable
argument_list|()
decl_stmt|;
if|if
condition|(
name|exe
operator|.
name|endsWith
argument_list|(
literal|"solr"
argument_list|)
condition|)
block|{
name|String
index|[]
name|args
init|=
name|cmd
operator|.
name|getArguments
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"start"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|hasFlag
argument_list|(
literal|"-cloud"
argument_list|,
name|args
argument_list|)
operator|&&
operator|!
name|hasFlag
argument_list|(
literal|"-c"
argument_list|,
name|args
argument_list|)
condition|)
return|return
name|startStandaloneSolr
argument_list|(
name|args
argument_list|)
return|;
name|File
name|baseDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|solrHomeDir
init|=
operator|new
name|File
argument_list|(
name|getArg
argument_list|(
literal|"-s"
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getArg
argument_list|(
literal|"-p"
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|JettyConfig
name|jettyConfig
init|=
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|setContext
argument_list|(
literal|"/solr"
argument_list|)
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|solrCloudCluster
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"host"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.port"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|solrCloudCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|1
argument_list|,
name|baseDir
argument_list|,
operator|new
name|File
argument_list|(
name|solrHomeDir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
name|jettyConfig
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// another member of this cluster -- not supported yet, due to how MiniSolrCloudCluster works
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only launching one SolrCloud node is supported by this test!"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"stop"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getArg
argument_list|(
literal|"-p"
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
comment|// stop the requested node
if|if
condition|(
name|standaloneSolr
operator|!=
literal|null
condition|)
block|{
name|int
name|localPort
init|=
name|standaloneSolr
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
name|localPort
condition|)
block|{
try|try
block|{
name|standaloneSolr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Stopped standalone Solr instance running on port "
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No Solr is running on port "
operator|+
name|port
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|solrCloudCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|solrCloudCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Stopped SolrCloud test cluster"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No Solr nodes found to stop!"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
else|else
block|{
name|String
name|cmdLine
init|=
name|joinArgs
argument_list|(
name|cmd
operator|.
name|getArguments
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmdLine
operator|.
name|indexOf
argument_list|(
literal|"post.jar"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// invocation of the post.jar file ... we'll just hit the SimplePostTool directly vs. trying to invoke another JVM
name|List
argument_list|<
name|String
argument_list|>
name|argsToSimplePostTool
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|afterPostJarArg
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|cmd
operator|.
name|getArguments
argument_list|()
control|)
block|{
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-D"
argument_list|)
condition|)
block|{
name|arg
operator|=
name|arg
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|eqPos
init|=
name|arg
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|arg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eqPos
argument_list|)
argument_list|,
name|arg
operator|.
name|substring
argument_list|(
name|eqPos
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|arg
operator|.
name|endsWith
argument_list|(
literal|"post.jar"
argument_list|)
condition|)
block|{
name|afterPostJarArg
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|afterPostJarArg
condition|)
block|{
name|argsToSimplePostTool
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|SimplePostTool
operator|.
name|main
argument_list|(
name|argsToSimplePostTool
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Executing command: "
operator|+
name|cmdLine
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|execute
argument_list|(
name|cmd
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Execute command ["
operator|+
name|cmdLine
operator|+
literal|"] failed due to: "
operator|+
name|exc
argument_list|,
name|exc
argument_list|)
expr_stmt|;
throw|throw
name|exc
throw|;
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|joinArgs
specifier|protected
name|String
name|joinArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
operator|||
name|args
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|a
init|=
literal|0
init|;
name|a
operator|<
name|args
operator|.
name|length
condition|;
name|a
operator|++
control|)
block|{
if|if
condition|(
name|a
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|args
index|[
name|a
index|]
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
DECL|method|startStandaloneSolr
specifier|protected
name|int
name|startStandaloneSolr
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|standaloneSolr
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Test is already running a standalone Solr instance "
operator|+
name|standaloneSolr
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"! This indicates a bug in the unit test logic."
argument_list|)
throw|;
block|}
if|if
condition|(
name|solrCloudCluster
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Test is already running a mini SolrCloud cluster! "
operator|+
literal|"This indicates a bug in the unit test logic."
argument_list|)
throw|;
block|}
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getArg
argument_list|(
literal|"-p"
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|solrHomeDir
init|=
operator|new
name|File
argument_list|(
name|getArg
argument_list|(
literal|"-s"
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"host"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.port"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|standaloneSolr
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|Thread
name|bg
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|standaloneSolr
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|bg
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|getArg
specifier|protected
name|String
name|getArg
parameter_list|(
name|String
name|arg
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|int
name|a
init|=
literal|0
init|;
name|a
operator|<
name|args
operator|.
name|length
condition|;
name|a
operator|++
control|)
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
name|args
index|[
name|a
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|a
operator|+
literal|1
operator|>=
name|args
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required value for the "
operator|+
name|arg
operator|+
literal|" option!"
argument_list|)
throw|;
return|return
name|args
index|[
name|a
operator|+
literal|1
index|]
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required arg "
operator|+
name|arg
operator|+
literal|" needed to execute command: "
operator|+
name|commandsExecuted
operator|.
name|get
argument_list|(
name|commandsExecuted
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|hasFlag
specifier|protected
name|boolean
name|hasFlag
parameter_list|(
name|String
name|flag
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
if|if
condition|(
name|flag
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|solrCloudCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|solrCloudCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to shutdown MiniSolrCloudCluster due to: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|standaloneSolr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|standaloneSolr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to shutdown standalone Solr due to: "
operator|+
name|exc
argument_list|)
expr_stmt|;
block|}
name|standaloneSolr
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|field|closeables
specifier|protected
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeables
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Closeable
name|toClose
range|:
name|closeables
control|)
block|{
try|try
block|{
name|toClose
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
name|closeables
operator|.
name|clear
argument_list|()
expr_stmt|;
name|closeables
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Fails intermittently on jenkins! Need to investigate this more.    */
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testTechproductsExample
specifier|public
name|void
name|testTechproductsExample
parameter_list|()
throws|throws
name|Exception
block|{
name|testExample
argument_list|(
literal|"techproducts"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchemalessExample
specifier|public
name|void
name|testSchemalessExample
parameter_list|()
throws|throws
name|Exception
block|{
name|testExample
argument_list|(
literal|"schemaless"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample
specifier|protected
name|void
name|testExample
parameter_list|(
name|String
name|exampleName
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|solrHomeDir
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SERVER_HOME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|solrHomeDir
operator|.
name|isDirectory
argument_list|()
condition|)
name|fail
argument_list|(
name|solrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found and is required to run this test!"
argument_list|)
expr_stmt|;
name|Path
name|tmpDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|File
name|solrExampleDir
init|=
name|tmpDir
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|solrServerDir
init|=
name|solrHomeDir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
comment|// need a port to start the example server on
name|int
name|bindPort
init|=
operator|-
literal|1
decl_stmt|;
try|try
init|(
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
name|bindPort
operator|=
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Selected port "
operator|+
name|bindPort
operator|+
literal|" to start "
operator|+
name|exampleName
operator|+
literal|" example Solr instance on ..."
argument_list|)
expr_stmt|;
name|String
index|[]
name|toolArgs
init|=
operator|new
name|String
index|[]
block|{
literal|"-e"
block|,
name|exampleName
block|,
literal|"-serverDir"
block|,
name|solrServerDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-exampleDir"
block|,
name|solrExampleDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-p"
block|,
name|String
operator|.
name|valueOf
argument_list|(
name|bindPort
argument_list|)
block|}
decl_stmt|;
comment|// capture tool output to stdout
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|stdoutSim
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|,
literal|true
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|RunExampleExecutor
name|executor
init|=
operator|new
name|RunExampleExecutor
argument_list|(
name|stdoutSim
argument_list|)
decl_stmt|;
name|closeables
operator|.
name|add
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|SolrCLI
operator|.
name|RunExampleTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|RunExampleTool
argument_list|(
name|executor
argument_list|,
name|System
operator|.
name|in
argument_list|,
name|stdoutSim
argument_list|)
decl_stmt|;
try|try
block|{
name|tool
operator|.
name|runTool
argument_list|(
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|toolArgs
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"RunExampleTool failed due to: "
operator|+
name|e
operator|+
literal|"; stdout from tool prior to failure: "
operator|+
name|baos
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|String
name|toolOutput
init|=
name|baos
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// dump all the output written by the SolrCLI commands to stdout
comment|//System.out.println("\n\n"+toolOutput+"\n\n");
name|File
name|exampleSolrHomeDir
init|=
operator|new
name|File
argument_list|(
name|solrExampleDir
argument_list|,
name|exampleName
operator|+
literal|"/solr"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exampleSolrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found! run "
operator|+
name|exampleName
operator|+
literal|" example failed; output: "
operator|+
name|toolOutput
argument_list|,
name|exampleSolrHomeDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"techproducts"
operator|.
name|equals
argument_list|(
name|exampleName
argument_list|)
condition|)
block|{
name|HttpSolrClient
name|solrClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
literal|"http://localhost:"
operator|+
name|bindPort
operator|+
literal|"/solr/"
operator|+
name|exampleName
argument_list|)
decl_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|qr
init|=
name|solrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|long
name|numFound
init|=
name|qr
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected 32 docs in the "
operator|+
name|exampleName
operator|+
literal|" example but found "
operator|+
name|numFound
operator|+
literal|", output: "
operator|+
name|toolOutput
argument_list|,
name|numFound
operator|==
literal|32
argument_list|)
expr_stmt|;
block|}
comment|// stop the test instance
name|executor
operator|.
name|execute
argument_list|(
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
operator|.
name|parse
argument_list|(
literal|"bin/solr stop -p "
operator|+
name|bindPort
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the interactive SolrCloud example; we cannot test the non-interactive because we need control over    * the port and can only test with one node since the test relies on setting the host and jetty.port system    * properties, i.e. there is no test coverage for the -noprompt option.    */
annotation|@
name|Test
DECL|method|testInteractiveSolrCloudExample
specifier|public
name|void
name|testInteractiveSolrCloudExample
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrHomeDir
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SERVER_HOME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|solrHomeDir
operator|.
name|isDirectory
argument_list|()
condition|)
name|fail
argument_list|(
name|solrHomeDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found and is required to run this test!"
argument_list|)
expr_stmt|;
name|Path
name|tmpDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|File
name|solrExampleDir
init|=
name|tmpDir
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|solrServerDir
init|=
name|solrHomeDir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|String
index|[]
name|toolArgs
init|=
operator|new
name|String
index|[]
block|{
literal|"-example"
block|,
literal|"cloud"
block|,
literal|"-serverDir"
block|,
name|solrServerDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-exampleDir"
block|,
name|solrExampleDir
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|int
name|bindPort
init|=
operator|-
literal|1
decl_stmt|;
try|try
init|(
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
name|bindPort
operator|=
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
block|}
name|String
name|collectionName
init|=
literal|"testCloudExamplePrompt"
decl_stmt|;
comment|// sthis test only support launching one SolrCloud node due to how MiniSolrCloudCluster works
comment|// and the need for setting the host and port system properties ...
name|String
name|userInput
init|=
literal|"1\n"
operator|+
name|bindPort
operator|+
literal|"\n"
operator|+
name|collectionName
operator|+
literal|"\n2\n2\ndata_driven_schema_configs\n"
decl_stmt|;
comment|// simulate user input from stdin
name|InputStream
name|userInputSim
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|userInput
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
comment|// capture tool output to stdout
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|stdoutSim
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|,
literal|true
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|RunExampleExecutor
name|executor
init|=
operator|new
name|RunExampleExecutor
argument_list|(
name|stdoutSim
argument_list|)
decl_stmt|;
name|closeables
operator|.
name|add
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|SolrCLI
operator|.
name|RunExampleTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|RunExampleTool
argument_list|(
name|executor
argument_list|,
name|userInputSim
argument_list|,
name|stdoutSim
argument_list|)
decl_stmt|;
try|try
block|{
name|tool
operator|.
name|runTool
argument_list|(
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|toolArgs
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"RunExampleTool failed due to: "
operator|+
name|e
operator|+
literal|"; stdout from tool prior to failure: "
operator|+
name|baos
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|String
name|toolOutput
init|=
name|baos
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// verify Solr is running on the expected port and verify the collection exists
name|String
name|solrUrl
init|=
literal|"http://localhost:"
operator|+
name|bindPort
operator|+
literal|"/solr"
decl_stmt|;
name|String
name|collectionListUrl
init|=
name|solrUrl
operator|+
literal|"/admin/collections?action=list"
decl_stmt|;
if|if
condition|(
operator|!
name|SolrCLI
operator|.
name|safeCheckCollectionExists
argument_list|(
name|collectionListUrl
argument_list|,
name|collectionName
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"After running Solr cloud example, test collection '"
operator|+
name|collectionName
operator|+
literal|"' not found in Solr at: "
operator|+
name|solrUrl
operator|+
literal|"; tool output: "
operator|+
name|toolOutput
argument_list|)
expr_stmt|;
block|}
comment|// index some docs - to verify all is good for both shards
name|CloudSolrClient
name|cloudClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cloudClient
operator|=
operator|new
name|CloudSolrClient
argument_list|(
name|executor
operator|.
name|solrCloudCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|connect
argument_list|()
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|int
name|numDocs
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDocs
condition|;
name|d
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"doc"
operator|+
name|d
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"str_s"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|qr
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"str_s:a"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|qr
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|!=
name|numDocs
condition|)
block|{
name|fail
argument_list|(
literal|"Expected "
operator|+
name|numDocs
operator|+
literal|" to be found in the "
operator|+
name|collectionName
operator|+
literal|" collection but only found "
operator|+
name|qr
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cloudClient
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cloudClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
block|}
name|File
name|node1SolrHome
init|=
operator|new
name|File
argument_list|(
name|solrExampleDir
argument_list|,
literal|"cloud/node1/solr"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|node1SolrHome
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|node1SolrHome
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found! run cloud example failed; tool output: "
operator|+
name|toolOutput
argument_list|)
expr_stmt|;
block|}
comment|// delete the collection
name|SolrCLI
operator|.
name|DeleteTool
name|deleteTool
init|=
operator|new
name|SolrCLI
operator|.
name|DeleteTool
argument_list|(
name|stdoutSim
argument_list|)
decl_stmt|;
name|String
index|[]
name|deleteArgs
init|=
operator|new
name|String
index|[]
block|{
literal|"-name"
block|,
name|collectionName
block|,
literal|"-solrUrl"
block|,
name|solrUrl
block|}
decl_stmt|;
name|deleteTool
operator|.
name|runTool
argument_list|(
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|deleteTool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|deleteArgs
argument_list|)
argument_list|)
expr_stmt|;
comment|// dump all the output written by the SolrCLI commands to stdout
comment|//System.out.println(toolOutput);
comment|// stop the test instance
name|executor
operator|.
name|execute
argument_list|(
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
operator|.
name|parse
argument_list|(
literal|"bin/solr stop -p "
operator|+
name|bindPort
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

