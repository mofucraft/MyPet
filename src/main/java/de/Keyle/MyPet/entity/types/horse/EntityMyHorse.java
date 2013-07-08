/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2013 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.entity.types.horse;

import de.Keyle.MyPet.entity.EntitySize;
import de.Keyle.MyPet.entity.types.EntityMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import net.minecraft.server.v1_6_R1.*;
import org.bukkit.Material;

@EntitySize(width = 1.4F, height = 1.6F)
public class EntityMyHorse extends EntityMyPet
{
    public static int GROW_UP_ITEM = Material.BREAD.getId();

    int soundCounter = 0;
    int rearCounter = -1;
    int ageCounter = -1;
    int ageFailCounter = 1;

    public EntityMyHorse(World world, MyPet myPet)
    {
        super(world, myPet);
    }

    public void setMyPet(MyPet myPet)
    {
        if (myPet != null)
        {
            super.setMyPet(myPet);

            this.setAge(((MyHorse) myPet).getAge());
            this.setHorseType(((MyHorse) myPet).getHorseType());
            this.setVariant(((MyHorse) myPet).getVariant());
            this.setSaddle(((MyHorse) myPet).hasSaddle());
            this.setChest(((MyHorse) myPet).hasChest());
        }
    }

    public void setChest(boolean flag)
    {
        applyVisual(8, flag);
        ((MyHorse) myPet).chest = flag;
    }

    public boolean hasChest()
    {
        return ((MyHorse) myPet).chest;
    }

    public void setSaddle(boolean flag)
    {
        applyVisual(4, flag);
        ((MyHorse) myPet).saddle = flag;
    }

    public boolean hasSaddle()
    {
        return ((MyHorse) myPet).saddle;
    }

    public void setHorseType(byte horseType)
    {
        this.datawatcher.watch(19, Byte.valueOf(horseType));
        ((MyHorse) myPet).horseType = horseType;
    }

    public byte getHorseType()
    {
        return ((MyHorse) myPet).horseType;
    }

    public void setArmor(int value)
    {
        this.datawatcher.watch(22, Integer.valueOf(value));
        ((MyHorse) myPet).armor = value;
    }

    public int getArmor()
    {
        return ((MyHorse) myPet).armor;
    }

    public void setVariant(int variant)
    {
        this.datawatcher.watch(20, Integer.valueOf(variant));
        ((MyHorse) myPet).variant = variant;
    }

    public int getVariant()
    {
        return ((MyHorse) myPet).variant;
    }

    public void setBaby(boolean flag)
    {
        if (flag)
        {
            this.datawatcher.watch(12, Integer.valueOf(-24000));
            ((MyHorse) myPet).age = -24000;
        }
        else
        {
            this.datawatcher.watch(12, new Integer(0));
            ((MyHorse) myPet).age = 0;
        }
    }

    public boolean isBaby()
    {
        return ((MyHorse) myPet).age < 0;
    }

    public void setAge(int value)
    {
        value = Math.min(0, (Math.max(-24000, value)));
        value -= value % 1000;
        ((MyHorse) myPet).age = value;
        this.datawatcher.watch(12, new Integer(value));
    }

    public int getAge()
    {
        return ((MyHorse) myPet).age;
    }

    public boolean attack(Entity entity)
    {
        boolean flag = false;
        try
        {
            flag = super.attack(entity);
            if (flag)
            {
                applyVisual(64, true);
                rearCounter = 10;
                if (getHorseType() == 0)
                {
                    this.world.makeSound(this, "mob.horse.angry", 1.0F, 1.0F);
                }
                else if (getHorseType() == 2 || getHorseType() == 1)
                {
                    this.world.makeSound(this, "mob.horse.donkey.angry", 1.0F, 1.0F);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }

    /*
     * Possible visual horse effects:
     *   4 saddle
     *   8 chest
     *   32 head down
     *   64 rear
     *   128 mouth open
     *
     */
    private void applyVisual(int value, boolean flag)
    {
        int i = this.datawatcher.getInt(16);
        if (flag)
        {
            this.datawatcher.watch(16, Integer.valueOf(i | value));
        }
        else
        {
            this.datawatcher.watch(16, Integer.valueOf(i & (~value)));
        }
    }

    // Obfuscated Methods -------------------------------------------------------------------------------------------

    protected void a()
    {
        super.a();
        this.datawatcher.a(12, Integer.valueOf(0));     // age
        this.datawatcher.a(16, Integer.valueOf(0));     // saddle & chest
        this.datawatcher.a(19, Byte.valueOf((byte) 0)); // horse type
        this.datawatcher.a(20, Integer.valueOf(0));     // variant
        this.datawatcher.a(21, String.valueOf(""));     // N/A
        this.datawatcher.a(22, Integer.valueOf(0));     // armor
    }

    /**
     * Is called when player rightclicks this MyPet
     * return:
     * true: there was a reaction on rightclick
     * false: no reaction on rightclick
     */
    public boolean a(EntityHuman entityhuman)
    {
        try
        {
            if (super.a(entityhuman))
            {
                return true;
            }
            ItemStack itemStack = entityhuman.inventory.getItemInHand();

            if (itemStack != null && canUseItem())
            {
                if (itemStack.id == 329 && getOwner().getPlayer().isSneaking() && !hasSaddle() && getAge() >= 0 && canEquip())
                {
                    setSaddle(true);
                    if (!entityhuman.abilities.canInstantlyBuild)
                    {
                        if (--itemStack.count <= 0)
                        {
                            entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                        }
                    }
                    return true;
                }
                else if (itemStack.id == 54 && getOwner().getPlayer().isSneaking() && !hasChest() && getAge() >= 0 && canEquip())
                {
                    setChest(true);
                    if (!entityhuman.abilities.canInstantlyBuild)
                    {
                        if (--itemStack.count <= 0)
                        {
                            entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                        }
                    }
                    return true;
                }
                else if (itemStack.id >= 417 && itemStack.id <= 419 && getOwner().getPlayer().isSneaking() && canEquip())
                {
                    if (getArmor() > 0 && !entityhuman.abilities.canInstantlyBuild)
                    {
                        EntityItem entityitem = this.a(new ItemStack(416 + getArmor(), 1, 0), 1F);
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    }
                    setArmor(itemStack.id - 416);
                    if (!entityhuman.abilities.canInstantlyBuild)
                    {
                        if (--itemStack.count <= 0)
                        {
                            entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                        }
                    }
                    return true;
                }
                else if (itemStack.id == Item.SHEARS.id && getOwner().getPlayer().isSneaking() && canEquip())
                {
                    if (getArmor() > 0 && !entityhuman.abilities.canInstantlyBuild)
                    {
                        setArmor(0);
                        EntityItem entityitem = this.a(new ItemStack(416 + getArmor(), 1, 0), 1F);
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    }
                    if (hasChest() && !entityhuman.abilities.canInstantlyBuild)
                    {
                        setChest(false);
                        EntityItem entityitem = this.a(new ItemStack(Block.CHEST), 1F);
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    }
                    if (hasSaddle() && !entityhuman.abilities.canInstantlyBuild)
                    {
                        setSaddle(false);
                        EntityItem entityitem = this.a(new ItemStack(Item.SADDLE), 1F);
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    }
                    return true;
                }
                else if (itemStack.id == GROW_UP_ITEM)
                {
                    if (isBaby())
                    {
                        if (getOwner().getPlayer().isSneaking())
                        {
                            if (!entityhuman.abilities.canInstantlyBuild)
                            {
                                if (--itemStack.count <= 0)
                                {
                                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                                }
                            }
                            this.setAge(getAge() + 3000);
                            return true;
                        }
                    }
                }
                if (itemStack.id == Material.BREAD.getId() ||
                        itemStack.id == Material.WHEAT.getId() ||
                        itemStack.id == Material.GOLDEN_APPLE.getId() ||
                        itemStack.id == Material.HAY_BLOCK.getId() ||
                        itemStack.id == Material.GOLDEN_CARROT.getId() ||
                        itemStack.id == Material.APPLE.getId() ||
                        itemStack.id == Material.SUGAR.getId())
                {
                    ageCounter = 5;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void a(int i, int j, int k, int l)
    {
        StepSound localStepSound = Block.byId[l].stepSound;
        if (this.world.getTypeId(i, j + 1, k) == Block.SNOW.id)
        {
            localStepSound = Block.SNOW.stepSound;
        }
        if (!Block.byId[l].material.isLiquid())
        {
            int horseType = ((MyHorse) myPet).horseType;
            if ((this.passenger != null) && (horseType != 1) && (horseType != 2))
            {
                this.soundCounter += 1;
                if ((this.soundCounter > 5) && (this.soundCounter % 3 == 0))
                {
                    makeSound("mob.horse.gallop", localStepSound.getVolume1() * 0.15F, localStepSound.getVolume2());
                    if ((horseType == 0) && (this.random.nextInt(10) == 0))
                    {
                        makeSound("mob.horse.breathe", localStepSound.getVolume1() * 0.6F, localStepSound.getVolume2());
                    }
                }
                else if (this.soundCounter <= 5)
                {
                    makeSound("mob.horse.wood", localStepSound.getVolume1() * 0.15F, localStepSound.getVolume2());
                }
            }
            else if (localStepSound == Block.h)
            {
                makeSound("mob.horse.soft", localStepSound.getVolume1() * 0.15F, localStepSound.getVolume2());
            }
            else
            {
                makeSound("mob.horse.wood", localStepSound.getVolume1() * 0.15F, localStepSound.getVolume2());
            }
        }
    }

    /**
     * Returns the sound that is played when the MyPet get hurt
     */
    @Override
    protected String aK()
    {
        int horseType = ((MyHorse) myPet).horseType;
        if (horseType == 3)
        {
            return "mob.horse.zombie.hit";
        }
        if (horseType == 4)
        {
            return "mob.horse.skeleton.hit";
        }
        if ((horseType == 1) || (horseType == 2))
        {
            return "mob.horse.donkey.hit";
        }
        return "mob.horse.hit";
    }

    /**
     * Returns the sound that is played when the MyPet dies
     */
    @Override
    protected String aL()
    {
        int horseType = ((MyHorse) myPet).horseType;
        if (horseType == 3)
        {
            return "mob.horse.zombie.death";
        }
        if (horseType == 4)
        {
            return "mob.horse.skeleton.death";
        }
        if ((horseType == 1) || (horseType == 2))
        {
            return "mob.horse.donkey.death";
        }
        return "mob.horse.death";
    }

    public void c()
    {
        super.c();
        if (rearCounter > -1 && rearCounter-- == 0)
        {
            applyVisual(64, false);
            rearCounter = -1;
        }
        if (ageCounter > -1 && ageCounter-- == 0)
        {
            this.datawatcher.watch(12, new Integer(getAge() + ageFailCounter++));
            ageCounter = -1;
            ageFailCounter %= 1000;
        }
    }

    /**
     * Returns the default sound of the MyPet
     */
    protected String r()
    {
        if (playIdleSound())
        {
            int horseType = ((MyHorse) myPet).horseType;
            if (horseType == 3)
            {
                return "mob.horse.zombie.idle";
            }
            if (horseType == 4)
            {
                return "mob.horse.skeleton.idle";
            }
            if ((horseType == 1) || (horseType == 2))
            {
                return "mob.horse.donkey.idle";
            }
            return "mob.horse.idle";
        }
        return "";
    }
}