package ru.practicum.shareit.util;

import org.hibernate.proxy.HibernateProxy;

/**
 * Вспомогательные методы для корректных {@code equals}/{@code hashCode} JPA-сущностей.
 * Учитывает Hibernate-прокси: без этого сравнение реальной сущности с её ленивым
 * прокси (другой Java-класс, тот же ряд в БД) ошибочно считалось бы неравным.
 */
public final class EntityUtils {

    private EntityUtils() {
    }

    /**
     * @param entity сущность или её Hibernate-прокси
     * @return реальный класс сущности (класс прокси разворачивается до класса персистентности)
     */
    public static Class<?> getEffectiveClass(Object entity) {
        return entity instanceof HibernateProxy
                ? ((HibernateProxy) entity).getHibernateLazyInitializer().getPersistentClass()
                : entity.getClass();
    }
}
