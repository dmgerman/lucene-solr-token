begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|document
operator|.
name|StringField
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
name|AtomicReader
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
name|IndexReader
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
name|RandomIndexWriter
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_class
DECL|class|TestFailOnFieldCacheInsanity
specifier|public
class|class
name|TestFailOnFieldCacheInsanity
extends|extends
name|WithNestedTests
block|{
DECL|method|TestFailOnFieldCacheInsanity
specifier|public
name|TestFailOnFieldCacheInsanity
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Nested1
specifier|public
specifier|static
class|class
name|Nested1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|field|d
specifier|private
name|Directory
name|d
decl_stmt|;
DECL|field|r
specifier|private
name|IndexReader
name|r
decl_stmt|;
DECL|field|subR
specifier|private
name|AtomicReader
name|subR
decl_stmt|;
DECL|method|makeIndex
specifier|private
name|void
name|makeIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we use RAMDirectory here, because we dont want to stay on open files on Windows:
name|d
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|d
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"ints"
argument_list|,
literal|"1"
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|w
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|subR
operator|=
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
expr_stmt|;
block|}
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex
argument_list|()
expr_stmt|;
comment|/* nocommit       assertNotNull(FieldCache.DEFAULT.getTermsIndex(subR, "ints"));       assertNotNull(FieldCache.DEFAULT.getTerms(subR, "ints", false));       */
comment|// NOTE: do not close reader/directory, else it
comment|// purges FC entries
block|}
block|}
comment|// nocommit: move this to solr?
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testFailOnFieldCacheInsanity
specifier|public
name|void
name|testFailOnFieldCacheInsanity
parameter_list|()
block|{
name|Result
name|r
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested1
operator|.
name|class
argument_list|)
decl_stmt|;
name|boolean
name|insane
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Failure
name|f
range|:
name|r
operator|.
name|getFailures
argument_list|()
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"Insane"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|insane
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|insane
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

