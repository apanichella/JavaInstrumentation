package nl.tudelft.instrumentation.general;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.util.LinkedList;

/**
 * This class implements a small line-coverage instrumentation tool. It relies on {@code JavaParser} for parsing Java
 * source code files and to visit the corresponding AST.
 *
 * @author Clinton Cao, Sicco Verwer
 */
public class BaseVisitor extends ModifierVisitor<Object> {
    int operator_nr = 0;

    LinkedList<String> operators = new LinkedList<String>();

    public Node addCode(Statement node, Statement new_statement, Object args){
        if (node.getParentNode().isPresent()){
        
            Node parent = node.getParentNode().get();

            if (parent instanceof BlockStmt) {
                // if {@code node} is within a BlockStmt (i.e., withing a block with
                // open-close curly brackets), we just add the new line for coverage tracking
                BlockStmt block = (BlockStmt) parent;
                int line = node.getBegin().get().line;
                int position = block.getStatements().indexOf(node);
                block.addStatement(position, new_statement);
            } else {
                // if {@code node} is not within a BlockStmt (e.g., true branch of an if condition
                // with no curly brackets), we need to create a BlockStmt first
                BlockStmt block = new BlockStmt();
                block.addStatement(new_statement);
                block.addStatement(node);
                return block;
            }
        }
        return node;
    }

    public Node addCodeAfter(Statement node, Statement new_statement, Object args){
        if (node.getParentNode().isPresent()){

            Node parent = node.getParentNode().get();

            if (parent instanceof BlockStmt) {
                // if {@code node} is within a BlockStmt (i.e., withing a block with
                // open-close curly brackets), we just add the new line for coverage tracking
                BlockStmt block = (BlockStmt) parent;
                int line = node.getBegin().get().line;
                int position = block.getStatements().indexOf(node) + 1;
                block.addStatement(position, new_statement);
            } else {
                // if {@code node} is not within a BlockStmt (e.g., true branch of an if condition
                // with no curly brackets), we need to create a BlockStmt first
                BlockStmt block = new BlockStmt();
                block.addStatement(node);
                block.addStatement(new_statement);
                return block;
            }
        }
        return node;
    }

}

