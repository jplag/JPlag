import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * PROJECT N: RPG Battle System
 * COMPLEXITY: Uses Interfaces, Abstract Classes, and Polymorphism.
 */
public class RPGBattleSystem {

    public static void main(String[] args) {
        System.out.println("--- Dungeon Brawl v1.0 ---");

        List<Combatant> arena = new ArrayList<>();
        arena.add(new Paladin("Sir Java", 100));
        arena.add(new Necromancer("Lord Null", 80));

        // Simulate rounds
        for (int i = 1; i <= 3; i++) {
            System.out.println("\nRound " + i);
            for (Combatant c : arena) {
                if (c.isAlive()) {
                    c.performAction();
                }
            }
        }

        //DeadCodeStart
        // Old loot drop table that was deprecated in favor of JSON configs
        boolean dropLoot = false;
        if (dropLoot) {
            String[] loot = {"Sword", "Shield", "Potion"};
            System.out.println("Dropped: " + loot[0]);
        }
        //DeadCodeEnd
    }

    // --- Interfaces & Hierarchy ---

    interface Combatant {
        void performAction();

        boolean isAlive();

        //DeadCodeStart
        void takeDamage(int amount);
        //DeadCodeEnd
    }

    abstract static class Character implements Combatant {
        String name;
        int hp;
        Random rng = new Random();

        Character(String n, int h) {
            this.name = n;
            this.hp = h;
        }

        public boolean isAlive() {
            return hp > 0;
        }

        //DeadCodeStart
        public void takeDamage(int amount) {
            this.hp -= amount;
            System.out.println(name + " takes " + amount + " damage. HP: " + hp);
        }
        //DeadCodeEnd
    }

    static class Paladin extends Character {
        Paladin(String n, int h) {
            super(n, h);
            System.out.println("Test");
        }

        @Override
        public void performAction() {
            // Paladin logic: High defense, physical attack
            int dmg = rng.nextInt(15) + 5;
            System.out.println(name + " swings Warhammer for " + dmg);

            //DeadCodeStart
            // Unused 'Holy Light' ability logic
            if (false) {
                this.hp += 50;
                System.out.println("Healed by Holy Light!");
            }
            //DeadCodeEnd
        }
    }

    static class Necromancer extends Character {
        Necromancer(String n, int h) {
            super(n, h);
        }

        @Override
        public void performAction() {
            // Necromancer logic: Magic attack
            int dmg = rng.nextInt(25);
            System.out.println(name + " casts Shadow Bolt for " + dmg);
        }
    }
}
