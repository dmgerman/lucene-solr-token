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
name|java
operator|.
name|io
operator|.
name|File
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
name|_TestUtil
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

begin_class
DECL|class|TestLeaveFilesIfTestFails
specifier|public
class|class
name|TestLeaveFilesIfTestFails
extends|extends
name|WithNestedTests
block|{
DECL|method|TestLeaveFilesIfTestFails
specifier|public
name|TestLeaveFilesIfTestFails
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
DECL|field|file
specifier|static
name|File
name|file
decl_stmt|;
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{
name|file
operator|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"leftover"
argument_list|)
expr_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLeaveFilesIfTestFails
specifier|public
name|void
name|testLeaveFilesIfTestFails
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Nested1
operator|.
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Nested1
operator|.
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

