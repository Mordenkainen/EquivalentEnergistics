package com.mordenkainen.equivalentenergistics.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mordenkainen.equivalentenergistics.integration.ee3.EquivExchange3;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import cpw.mods.fml.common.Loader;
import net.minecraft.launchwrapper.IClassTransformer;

public class EqECoreTransformer implements IClassTransformer {

    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {

        if ("com.pahimar.ee3.knowledge.PlayerKnowledgeRegistry".equals(name)) {
            if (Loader.isModLoaded("EE3")) {
                final ClassReader cr = new ClassReader(basicClass);

                final ClassNode classNode = new ClassNode();
                cr.accept(classNode, 0);

                for (final MethodNode methodNode : classNode.methods) {
                    if ("teachPlayer".equals(methodNode.name) && "(Ljava/lang/String;Ljava/lang/Object;)V".equals(methodNode.desc)) {
                        patchTeachPlayer(methodNode);
                    }

                    if ("teachPlayer".equals(methodNode.name) && "(Ljava/lang/String;Ljava/util/Collection;)V".equals(methodNode.desc)) {
                        patchTeachPlayerCollection(methodNode);
                    }

                    if ("makePlayerForget".equals(methodNode.name) && "(Ljava/lang/String;Ljava/lang/Object;)V".equals(methodNode.desc)) {
                        patchMakePlayerForget(methodNode);
                    }

                    if ("makePlayerForget".equals(methodNode.name) && "(Ljava/lang/String;Ljava/util/Collection;)V".equals(methodNode.desc)) {
                        patchMakePlayerForgetCollection(methodNode);
                    }

                    if (methodNode.name.equals("makePlayerForgetAll") && methodNode.desc.equals("(Ljava/lang/String;)V")) { // NOPMD
                        patchMakePlayerForgetAll(methodNode);
                    }
                }

                final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                classNode.accept(cw);

                return cw.toByteArray();
            }
        }

        return basicClass;
    }

    private static void patchTeachPlayer(final MethodNode methodNode) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), "postPlayerLearn", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insertBefore(insn, endList);
            }
        }
    }

    private static void patchTeachPlayerCollection(final MethodNode methodNode) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (insn.getOpcode() == Opcodes.GOTO) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), "postPlayerLearn", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insert(insn, endList);
            }
        }
    }

    private void patchMakePlayerForget(final MethodNode methodNode) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), "postPlayerForget", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insertBefore(insn, endList);
            }
        }
    }

    private void patchMakePlayerForgetCollection(final MethodNode methodNode) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (insn.getOpcode() == Opcodes.GOTO) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), "postPlayerForget", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insert(insn, endList);
            }
        }
    }

    private void patchMakePlayerForgetAll(final MethodNode methodNode) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (insn.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) insn).var == 0) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), "postPlayerForget", "(Ljava/lang/String;)V", false));
                methodNode.instructions.insertBefore(insn, endList);
            }

        }
    }

}
