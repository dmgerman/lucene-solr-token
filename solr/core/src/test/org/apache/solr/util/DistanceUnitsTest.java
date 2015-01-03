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

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|DistanceUnitsTest
specifier|public
class|class
name|DistanceUnitsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testAddNewUnits
specifier|public
name|void
name|testAddNewUnits
parameter_list|()
throws|throws
name|Exception
block|{
name|DistanceUnits
operator|.
name|addUnits
argument_list|(
literal|"lightyears"
argument_list|,
literal|6.73430542e-12
argument_list|,
literal|9.4605284e12
operator|*
name|DistanceUtils
operator|.
name|KM_TO_DEG
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DistanceUnits
operator|.
name|getSupportedUnits
argument_list|()
operator|.
name|contains
argument_list|(
literal|"lightyears"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

