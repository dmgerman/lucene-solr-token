begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package

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
name|File
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|Constants
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
name|AbstractZkTestCase
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
name|hadoop
operator|.
name|dedup
operator|.
name|NoChangeUpdateConflictResolver
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
name|hadoop
operator|.
name|dedup
operator|.
name|RetainMostRecentUpdateConflictResolver
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
name|Before
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
DECL|class|MapReduceIndexerToolArgumentParserTest
specifier|public
class|class
name|MapReduceIndexerToolArgumentParserTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|parser
specifier|private
name|MapReduceIndexerTool
operator|.
name|MyArgumentParser
name|parser
decl_stmt|;
DECL|field|opts
specifier|private
name|MapReduceIndexerTool
operator|.
name|Options
name|opts
decl_stmt|;
DECL|field|oldSystemOut
specifier|private
name|PrintStream
name|oldSystemOut
decl_stmt|;
DECL|field|oldSystemErr
specifier|private
name|PrintStream
name|oldSystemErr
decl_stmt|;
DECL|field|bout
specifier|private
name|ByteArrayOutputStream
name|bout
decl_stmt|;
DECL|field|berr
specifier|private
name|ByteArrayOutputStream
name|berr
decl_stmt|;
DECL|field|RESOURCES_DIR
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCES_DIR
init|=
name|getFile
argument_list|(
literal|"morphlines-core.marker"
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
DECL|field|MINIMR_INSTANCE_DIR
specifier|private
specifier|static
specifier|final
name|File
name|MINIMR_INSTANCE_DIR
init|=
operator|new
name|File
argument_list|(
name|RESOURCES_DIR
operator|+
literal|"/solr/minimr"
argument_list|)
decl_stmt|;
DECL|field|MORPHLINE_FILE
specifier|private
specifier|static
specifier|final
name|String
name|MORPHLINE_FILE
init|=
name|RESOURCES_DIR
operator|+
literal|"/test-morphlines/solrCellDocumentTypes.conf"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MapReduceIndexerToolArgumentParserTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|solrHomeDirectory
specifier|private
specifier|static
specifier|final
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|MorphlineGoLiveMiniMRTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
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
name|assumeFalse
argument_list|(
literal|"Does not work on Windows, because it uses UNIX shell commands or POSIX paths"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
name|solrHomeDirectory
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|MINIMR_INSTANCE_DIR
argument_list|,
name|solrHomeDirectory
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|MapReduceIndexerTool
operator|.
name|MyArgumentParser
argument_list|()
expr_stmt|;
name|opts
operator|=
operator|new
name|MapReduceIndexerTool
operator|.
name|Options
argument_list|()
expr_stmt|;
name|oldSystemOut
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|bout
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bout
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|oldSystemErr
operator|=
name|System
operator|.
name|err
expr_stmt|;
name|berr
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|berr
argument_list|,
literal|true
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|System
operator|.
name|setOut
argument_list|(
name|oldSystemOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|oldSystemErr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserTypicalUse
specifier|public
name|void
name|testArgsParserTypicalUse
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--morphline-id"
block|,
literal|"morphline_xyz"
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--mappers"
block|,
literal|"10"
block|,
literal|"--reducers"
block|,
literal|"9"
block|,
literal|"--fanout"
block|,
literal|"8"
block|,
literal|"--max-segments"
block|,
literal|"7"
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--verbose"
block|,
literal|"file:///home"
block|,
literal|"file:///dev"
block|,         }
decl_stmt|;
name|Integer
name|res
init|=
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|res
operator|!=
literal|null
condition|?
name|res
operator|.
name|toString
argument_list|()
else|:
literal|""
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputLists
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:/tmp/foo"
argument_list|)
argument_list|,
name|opts
operator|.
name|outputDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|opts
operator|.
name|solrHomeDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|opts
operator|.
name|mappers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|opts
operator|.
name|reducers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|opts
operator|.
name|fanout
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|opts
operator|.
name|maxSegments
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|opts
operator|.
name|shards
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|opts
operator|.
name|fairSchedulerPool
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|opts
operator|.
name|isVerbose
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///home"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dev"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputFiles
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetainMostRecentUpdateConflictResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|opts
operator|.
name|updateConflictResolver
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MORPHLINE_FILE
argument_list|,
name|opts
operator|.
name|morphlineFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"morphline_xyz"
argument_list|,
name|opts
operator|.
name|morphlineId
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserMultipleSpecsOfSameKind
specifier|public
name|void
name|testArgsParserMultipleSpecsOfSameKind
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--input-list"
block|,
literal|"file:///"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"file:///home"
block|,
literal|"file:///dev"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputLists
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///home"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dev"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputFiles
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:/tmp/foo"
argument_list|)
argument_list|,
name|opts
operator|.
name|outputDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|opts
operator|.
name|solrHomeDir
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserTypicalUseWithEqualsSign
specifier|public
name|void
name|testArgsParserTypicalUseWithEqualsSign
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list=file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir=file:/tmp/foo"
block|,
literal|"--solr-home-dir="
operator|+
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--mappers=10"
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--verbose"
block|,
literal|"file:///home"
block|,
literal|"file:///dev"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputLists
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:/tmp/foo"
argument_list|)
argument_list|,
name|opts
operator|.
name|outputDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|opts
operator|.
name|solrHomeDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|opts
operator|.
name|mappers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|opts
operator|.
name|shards
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|opts
operator|.
name|fairSchedulerPool
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|opts
operator|.
name|isVerbose
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///home"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dev"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputFiles
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserMultipleSpecsOfSameKindWithEqualsSign
specifier|public
name|void
name|testArgsParserMultipleSpecsOfSameKindWithEqualsSign
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list=file:///tmp"
block|,
literal|"--input-list=file:///"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir=file:/tmp/foo"
block|,
literal|"--solr-home-dir="
operator|+
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"file:///home"
block|,
literal|"file:///dev"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputLists
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///home"
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///dev"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|inputFiles
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:/tmp/foo"
argument_list|)
argument_list|,
name|opts
operator|.
name|outputDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|opts
operator|.
name|solrHomeDir
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserHelp
specifier|public
name|void
name|testArgsParserHelp
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|0
argument_list|)
argument_list|,
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|helpText
init|=
operator|new
name|String
argument_list|(
name|bout
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|helpText
operator|.
name|contains
argument_list|(
literal|"MapReduce batch job driver that "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helpText
operator|.
name|contains
argument_list|(
literal|"bin/hadoop command"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|berr
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserOk
specifier|public
name|void
name|testArgsParserOk
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|opts
operator|.
name|shards
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserUpdateConflictResolver
specifier|public
name|void
name|testArgsParserUpdateConflictResolver
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--update-conflict-resolver"
block|,
name|NoChangeUpdateConflictResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NoChangeUpdateConflictResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|opts
operator|.
name|updateConflictResolver
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserUnknownArgName
specifier|public
name|void
name|testArgsParserUnknownArgName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--xxxxxxxxinputlist"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserFileNotFound1
specifier|public
name|void
name|testArgsParserFileNotFound1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/fileNotFound/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserFileNotFound2
specifier|public
name|void
name|testArgsParserFileNotFound2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
literal|"/fileNotFound"
block|,
literal|"--shards"
block|,
literal|"1"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserIntOutOfRange
specifier|public
name|void
name|testArgsParserIntOutOfRange
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--mappers"
block|,
literal|"-20"
block|}
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserIllegalFanout
specifier|public
name|void
name|testArgsParserIllegalFanout
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--fanout"
block|,
literal|"1"
comment|// must be>= 2
block|}
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParserSolrHomeMustContainSolrConfigFile
specifier|public
name|void
name|testArgsParserSolrHomeMustContainSolrConfigFile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--solr-home-dir"
block|,
literal|"/"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsShardUrlOk
specifier|public
name|void
name|testArgsShardUrlOk
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection2"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"http://localhost:8983/solr/collection1"
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"http://localhost:8983/solr/collection2"
argument_list|)
argument_list|)
argument_list|,
name|opts
operator|.
name|shardUrls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
argument_list|,
name|opts
operator|.
name|shards
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsShardUrlMustHaveAParam
specifier|public
name|void
name|testArgsShardUrlMustHaveAParam
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shard-url"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsShardUrlAndShardsSucceeds
specifier|public
name|void
name|testArgsShardUrlAndShardsSucceeds
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shards"
block|,
literal|"1"
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsShardUrlNoGoLive
specifier|public
name|void
name|testArgsShardUrlNoGoLive
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|}
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|opts
operator|.
name|shards
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsShardUrlsAndZkhostAreMutuallyExclusive
specifier|public
name|void
name|testArgsShardUrlsAndZkhostAreMutuallyExclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,
literal|"--zk-host"
block|,
literal|"http://localhost:2185"
block|,
literal|"--go-live"
block|}
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsGoLiveAndSolrUrl
specifier|public
name|void
name|testArgsGoLiveAndSolrUrl
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,
literal|"--shard-url"
block|,
literal|"http://localhost:8983/solr/collection1"
block|,
literal|"--go-live"
block|}
decl_stmt|;
name|Integer
name|result
init|=
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsZkHostNoGoLive
specifier|public
name|void
name|testArgsZkHostNoGoLive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--zk-host"
block|,
literal|"http://localhost:2185"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsGoLiveZkHostNoCollection
specifier|public
name|void
name|testArgsGoLiveZkHostNoCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--zk-host"
block|,
literal|"http://localhost:2185"
block|,
literal|"--go-live"
block|}
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsGoLiveNoZkHostOrSolrUrl
specifier|public
name|void
name|testArgsGoLiveNoZkHostOrSolrUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--solr-home-dir"
block|,
name|MINIMR_INSTANCE_DIR
operator|.
name|getPath
argument_list|()
block|,
literal|"--go-live"
block|}
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoSolrHomeDirOrZKHost
specifier|public
name|void
name|testNoSolrHomeDirOrZKHost
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--shards"
block|,
literal|"1"
block|,         }
decl_stmt|;
name|assertArgumentParserException
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testZKHostNoSolrHomeDirOk
specifier|public
name|void
name|testZKHostNoSolrHomeDirOk
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--input-list"
block|,
literal|"file:///tmp"
block|,
literal|"--morphline-file"
block|,
name|MORPHLINE_FILE
block|,
literal|"--output-dir"
block|,
literal|"file:/tmp/foo"
block|,
literal|"--zk-host"
block|,
literal|"http://localhost:2185"
block|,
literal|"--collection"
block|,
literal|"collection1"
block|,         }
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEmptySystemErrAndEmptySystemOut
argument_list|()
expr_stmt|;
block|}
DECL|method|assertEmptySystemErrAndEmptySystemOut
specifier|private
name|void
name|assertEmptySystemErrAndEmptySystemOut
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bout
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|berr
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|assertArgumentParserException
specifier|private
name|void
name|assertArgumentParserException
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|assertEquals
argument_list|(
literal|"should have returned fail code"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|parser
operator|.
name|parseArgs
argument_list|(
name|args
argument_list|,
name|conf
argument_list|,
name|opts
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no sys out expected:"
operator|+
operator|new
name|String
argument_list|(
name|bout
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bout
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|usageText
decl_stmt|;
name|usageText
operator|=
operator|new
name|String
argument_list|(
name|berr
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should start with usage msg \"usage: hadoop \":"
operator|+
name|usageText
argument_list|,
name|usageText
operator|.
name|startsWith
argument_list|(
literal|"usage: hadoop "
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

