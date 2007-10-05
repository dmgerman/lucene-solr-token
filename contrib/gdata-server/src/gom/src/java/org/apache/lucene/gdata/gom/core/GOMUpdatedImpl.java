begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|gdata
operator|.
name|gom
operator|.
name|GOMNamespace
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
name|gdata
operator|.
name|gom
operator|.
name|GOMUpdated
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
name|gdata
operator|.
name|gom
operator|.
name|core
operator|.
name|utils
operator|.
name|GOMUtils
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
name|gdata
operator|.
name|gom
operator|.
name|writer
operator|.
name|GOMOutputWriter
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|GOMUpdatedImpl
specifier|public
class|class
name|GOMUpdatedImpl
extends|extends
name|GOMDateConstructImpl
implements|implements
name|GOMUpdated
block|{
DECL|field|ATOM_QNAME
specifier|protected
specifier|static
specifier|final
name|QName
name|ATOM_QNAME
init|=
operator|new
name|QName
argument_list|(
name|GOMNamespace
operator|.
name|ATOM_NS_URI
argument_list|,
name|LOCALNAME
argument_list|,
name|GOMNamespace
operator|.
name|ATOM_NS_PREFIX
argument_list|)
decl_stmt|;
comment|/** 	 *  	 */
DECL|method|GOMUpdatedImpl
specifier|public
name|GOMUpdatedImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|LOCALNAME
expr_stmt|;
name|this
operator|.
name|qname
operator|=
operator|new
name|QName
argument_list|(
name|GOMNamespace
operator|.
name|ATOM_NS_URI
argument_list|,
name|this
operator|.
name|localName
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.lucene.gdata.gom.GOMElement#writeRssOutput(org.apache.lucene.gdata.gom.writer.GOMOutputWriter) 	 */
DECL|method|writeRssOutput
specifier|public
name|void
name|writeRssOutput
parameter_list|(
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
name|this
operator|.
name|rfc3339String
operator|==
literal|null
condition|)
name|this
operator|.
name|rfc3339String
operator|=
name|GOMUtils
operator|.
name|buildRfc3339DateFormat
argument_list|(
name|this
operator|.
name|date
operator|==
literal|0
condition|?
name|System
operator|.
name|currentTimeMillis
argument_list|()
else|:
name|this
operator|.
name|date
argument_list|)
expr_stmt|;
name|aStreamWriter
operator|.
name|writeSimpleXMLElement
argument_list|(
name|ATOM_QNAME
argument_list|,
name|getXmlNamespaceAttributes
argument_list|()
argument_list|,
name|this
operator|.
name|rfc3339String
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

