package com.mordenkainen.equivalentenergistics.asm;

import java.util.Iterator;
import java.util.function.BiPredicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.Type;

import com.mordenkainen.equivalentenergistics.integration.ee3.EquivExchange3;

import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;

public class EqECoreTransformer implements IClassTransformer {

    private static BiPredicate<AbstractInsnNode, Integer> standardTest = (n, i) -> n.getOpcode() == i;
    private static BiPredicate<AbstractInsnNode, Integer> forgetAllTest = (n, i) -> n.getOpcode() == i && ((VarInsnNode) n).var == 0;

    private enum patches {
        LEARN("teachPlayer", "(Ljava/lang/String;Ljava/lang/Object;)V", Opcodes.INVOKEVIRTUAL, standardTest, true, "postPlayerLearn"),
        LEARNCOL("teachPlayer", "(Ljava/lang/String;Ljava/util/Collection;)V", Opcodes.GOTO, standardTest, false, "postPlayerLearn"),
        FORGET("makePlayerForget", "(Ljava/lang/String;Ljava/lang/Object;)V", Opcodes.INVOKEVIRTUAL, standardTest, true, "postPlayerForget"),
        FORGETCOL("makePlayerForget", "(Ljava/lang/String;Ljava/util/Collection;)V", Opcodes.GOTO, standardTest, false, "postPlayerForget"),
        FORGETALL("makePlayerForgetAll", "(Ljava/lang/String;)V", Opcodes.ALOAD, forgetAllTest, true, "postPlayerForget");

        public String method;
        public String desc;
        public int opcode;
        public BiPredicate<AbstractInsnNode, Integer> test;
        public boolean before;
        public String event;

        patches(final String method, final String desc, final int opcode, final BiPredicate<AbstractInsnNode, Integer> test, final boolean before, final String event) {
            this.method = method;
            this.desc = desc;
            this.opcode = opcode;
            this.test = test;
            this.before = before;
            this.event = event;
        }

    }

    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        if ("com.pahimar.ee3.knowledge.PlayerKnowledgeRegistry".equals(name)) {

            final ClassReader cr = new ClassReader(basicClass);
    
            final ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);
    
            for (final MethodNode methodNode : classNode.methods) {
                for (final patches patch : patches.values()) {
                    if (methodNode.name.equals(patch.method) && methodNode.desc.equals(patch.desc)) {
                        applyPatch(methodNode, patch.opcode, patch.test, patch.before, patch.event);
                    }
                }
            }
    
            final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
    
            return cw.toByteArray();
        }
        
        if ("moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock".equals(name) || "moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput".equals(name)) {
            final ClassReader cr = new ClassReader(basicClass);
            
            final ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);
    
            for (final MethodNode methodNode : classNode.methods) {
                if ("func_75214_a".equals(methodNode.name) || "isItemValid".equals(methodNode.name)) {
                    final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
                    boolean found = false;
                    while (insnNodes.hasNext() && !found) {
                        final AbstractInsnNode insn = insnNodes.next();
                        if (insn.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) insn).var == 1) {
                            found = true;
                            LabelNode label1 = new LabelNode();
                            final InsnList endList = new InsnList();
                            endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            endList.add(new JumpInsnNode(Opcodes.IFNULL, label1));
                            endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            endList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", EqECoreLoader.DEOBF ? "getItem" : "func_77973_b", "()Lnet/minecraft/item/Item;", false));
                            endList.add(new TypeInsnNode(Opcodes.INSTANCEOF, "moze_intel/projecte/api/item/IItemEmc"));
                            endList.add(new JumpInsnNode(Opcodes.IFEQ, label1));
                            endList.add(new InsnNode(Opcodes.ICONST_1));
                            endList.add(new InsnNode(Opcodes.IRETURN));
                            endList.add(label1);
                            methodNode.instructions.insertBefore(insn, endList);
                        }
                    }
                }
            }
            
            final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);
    
            return cw.toByteArray();
        }
        
        return basicClass;
    }

    private static void applyPatch(final MethodNode methodNode, final int opcode, final BiPredicate<AbstractInsnNode, Integer> test, final boolean before, final String event) {
        final Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
        boolean found = false;
        while (insnNodes.hasNext() && !found) {
            final AbstractInsnNode insn = insnNodes.next();

            if (test.test(insn, opcode)) {
                found = true;
                final InsnList endList = new InsnList();
                endList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                endList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EquivExchange3.class), event, "(Ljava/lang/String;)V", false));
                if (before) {
                    methodNode.instructions.insertBefore(insn, endList);
                } else {
                    methodNode.instructions.insert(insn, endList);
                }
            }
        }
    }
    
    public boolean testfunc(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IItemEmc) {
            return true;
        }
        return false;
    }

}
