begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  */
end_comment

begin_package
DECL|package|hplb.org.w3c.dom
package|package
name|hplb
operator|.
name|org
operator|.
name|w3c
operator|.
name|dom
package|;
end_package

begin_comment
comment|/**  *   */
end_comment

begin_interface
DECL|interface|NodeIterator
specifier|public
interface|interface
name|NodeIterator
block|{
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
function_decl|;
DECL|method|getCurrent
specifier|public
name|Node
name|getCurrent
parameter_list|()
function_decl|;
DECL|method|toNext
specifier|public
name|Node
name|toNext
parameter_list|()
function_decl|;
DECL|method|toPrevious
specifier|public
name|Node
name|toPrevious
parameter_list|()
function_decl|;
DECL|method|toFirst
specifier|public
name|Node
name|toFirst
parameter_list|()
function_decl|;
DECL|method|toLast
specifier|public
name|Node
name|toLast
parameter_list|()
function_decl|;
DECL|method|toNth
specifier|public
name|Node
name|toNth
parameter_list|(
name|int
name|Nth
parameter_list|)
function_decl|;
DECL|method|toNode
specifier|public
name|Node
name|toNode
parameter_list|(
name|Node
name|destNode
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

